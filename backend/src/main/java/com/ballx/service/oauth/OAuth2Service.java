package com.ballx.service.oauth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballx.config.jwt.JwtTokenProvider;
import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.request.oauth.OAuth2LinkRequest;
import com.ballx.domain.dto.request.oauth.OAuth2SignUpRequest;
import com.ballx.domain.dto.response.oauth.OAuth2AccountResponse;
import com.ballx.domain.dto.response.oauth.OAuth2LinkResponse;
import com.ballx.domain.dto.response.oauth.OAuth2StatusResponse;
import com.ballx.domain.dto.response.oauth.OAuth2TokenResponse;
import com.ballx.domain.entity.oauth.SocialProviderEntity;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.exception.handler.BusinessException;
import com.ballx.infra.oauth.OAuth2UserUnlinkManager;
import com.ballx.repository.oauth.OAuth2Repository;
import com.ballx.repository.user.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2Service {
	private final OAuth2Repository oAuth2Repository;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;

	public OAuth2StatusResponse checkOAuth2Status(
		ProviderType provider,
		String providerId,
		String email
	) {
		Optional<SocialProviderEntity> checkOAuth2Entity =
			oAuth2Repository.findByProviderAndProviderId(provider, providerId);

		if (checkOAuth2Entity.isPresent()) {
			SocialProviderEntity socialProviderEntity = checkOAuth2Entity.get();
			MemberEntity member = socialProviderEntity.getMember();

			return OAuth2StatusResponse.login(
				provider,
				email,
				member.getMobile()
			);
		}
		return OAuth2StatusResponse.needSignup(provider, email);
	}

	@Transactional
	public OAuth2TokenResponse signUp(
		OAuth2SignUpRequest request,
		ProviderType provider,
		String providerId,
		String email
	) {
		if (memberRepository.existsByMobile(request.mobile())) {
			throw new BusinessException("이미 가입 되어 있는 번호입니다.");
		}

		if (oAuth2Repository.findByProviderAndProviderId(provider, providerId).isPresent()) {
			throw new BusinessException("이미 연동 되어 있는 소셜계정입니다.");
		}

		MemberEntity member = MemberEntity.create(
			request.name(),
			request.mobile(),
			request.gender(),
			request.birthDate()
		);
		memberRepository.save(member);

		SocialProviderEntity socialProviderEntity = SocialProviderEntity.create(
			member,
			provider,
			providerId,
			email
		);
		oAuth2Repository.save(socialProviderEntity);

		String accessToken = jwtTokenProvider.create(
			member.getId(),
			member.getMobile(),
			member.getRole()
		);

		return OAuth2TokenResponse.of(accessToken, 3600000L);
	}

	@Transactional
	public OAuth2LinkResponse linkAccount(
		OAuth2LinkRequest request,
		ProviderType provider,
		String providerId,
		String email
	) {
		MemberEntity member = memberRepository.findByMobile(request.mobile())
			.orElseThrow(() -> new BusinessException("등록되지 않은 휴대폰 번호입니다."));

		if (oAuth2Repository.existsByMemberAndProvider(member, provider)) {
			throw new BusinessException("이미 연동된 소셜 계정입니다.");
		}

		SocialProviderEntity socialProviderEntity = SocialProviderEntity.create(
			member,
			provider,
			providerId,
			email
		);
		oAuth2Repository.save(socialProviderEntity);

		return OAuth2LinkResponse.of(provider, email);
	}

	public OAuth2TokenResponse login(
		ProviderType provider,
		String providerId
	) {
		SocialProviderEntity socialProviderEntity = oAuth2Repository
			.findByProviderAndProviderId(provider, providerId)
			.orElseThrow(() -> new BusinessException("등록되지 않은 소셜 계정입니다."));

		MemberEntity member = socialProviderEntity.getMember();

		String accessToken = jwtTokenProvider.create(
			member.getId(),
			member.getMobile(),
			member.getRole()
		);

		return OAuth2TokenResponse.of(accessToken, 3600000L);
	}

	public boolean existsByMobile(String mobile) {
		return memberRepository.existsByMobile(mobile);
	}

	@Transactional
	public void unlinkOAuth2Account(
		UUID memberId,
		ProviderType provider,
		String providerId,
		String accessToken
	) {
		MemberEntity member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException("멤버를 찾을 수 없습니다."));

		SocialProviderEntity socialProviderEntity = oAuth2Repository
			.findByProviderAndProviderId(provider, providerId)
			.orElseThrow(() -> new BusinessException("연동된 소셜 계정을 찾을 수 없습니다."));

		if (!socialProviderEntity.getMember().getId().equals(member.getId())) {
			throw new BusinessException("본인 계정이 아닙니다.");
		}

		long oAuth2Count = oAuth2Repository.countByMember(member);
		if (oAuth2Count <= 1) {
			throw new BusinessException("최소 한 개의 소셜 계정은 유지해야 합니다.");
		}

		try {
			oAuth2UserUnlinkManager.unlink(provider, accessToken);
			log.info("연동 해제 성공 : Provider: {}, MemberId: {}", provider, memberId);
		} catch (Exception e) {
			log.error("연동 해제 실패 Provider: {}", e.getMessage());
		}

		oAuth2Repository.delete(socialProviderEntity);
		log.info("연동 해제 완료 : Provider: {}, MemberId: {}", provider, memberId);
	}

	public List<OAuth2AccountResponse> getLinkedOAuth2Accounts(UUID memberId) {
		MemberEntity member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException("멤버를 찾을 수 없습니다."));
		List<SocialProviderEntity> linkedAccounts = oAuth2Repository.findALlByMember(member);

		return linkedAccounts.stream().map(this::mapToOAuth2AccountResponse).collect(Collectors.toList());
	}

	private OAuth2AccountResponse mapToOAuth2AccountResponse(SocialProviderEntity entity) {
		return new OAuth2AccountResponse(
			entity.getProvider(),
			entity.getEmail(),
			entity.getCreatedAt()
		);
	}
}
