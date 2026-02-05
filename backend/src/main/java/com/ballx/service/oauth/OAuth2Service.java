package com.ballx.service.oauth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballx.config.jwt.JwtTokenProvider;
import com.ballx.constants.OAuth2Provider;
import com.ballx.domain.dto.request.oauth.OAuth2LinkRequest;
import com.ballx.domain.dto.request.oauth.OAuth2SignUpRequest;
import com.ballx.domain.dto.response.oauth.OAuth2AccountResponse;
import com.ballx.domain.dto.response.oauth.OAuth2LinkResponse;
import com.ballx.domain.dto.response.oauth.OAuth2StatusResponse;
import com.ballx.domain.dto.response.oauth.OAuth2TokenResponse;
import com.ballx.domain.entity.oauth.OAuth2Entity;
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

	// 로그인 시도
	public OAuth2StatusResponse checkOAuth2Status(
		OAuth2Provider provider,
		String providerId,
		String email
	) {
		// 해당 소셜 계정으로 가입된 OAuth2Entity 확인
		Optional<OAuth2Entity> checkOAuth2Entity =
			oAuth2Repository.findByProviderAndProviderId(provider, providerId);

		if (checkOAuth2Entity.isPresent()) {
			// 기존 회원 - 로그인 완료
			OAuth2Entity oAuth2Entity = checkOAuth2Entity.get();
			MemberEntity member = oAuth2Entity.getMember();

			return OAuth2StatusResponse.login(
				provider,
				email,
				member.getMobile()
			);
		}
		// 신규 회원 - 회원 가입 필요
		return OAuth2StatusResponse.needSignup(provider, email);
	}

	// 회원 가입
	@Transactional
	public OAuth2TokenResponse signUp(
		OAuth2SignUpRequest request,
		OAuth2Provider provider,
		String providerId,
		String email
	) {
		// 번호로 기존 멤버 확인
		if (memberRepository.existsByMobile(request.mobile())) {
			throw new BusinessException("이미 가입 되어 있는 번호입니다.");
		}

		// 이미 연동 된 소셜인지 확인
		if (oAuth2Repository.findByProviderAndProviderId(provider, providerId).isPresent()) {
			throw new BusinessException("이미 연동 되어 있는 소셜계정입니다.");
		}

		// 멤버 생성
		MemberEntity member = MemberEntity.create(
			request.name(),
			request.mobile(),
			request.gender(),
			request.birthDate()
		);
		memberRepository.save(member);

		// 소셜 연동
		OAuth2Entity oAuth2Entity = OAuth2Entity.create(
			member,
			provider,
			providerId,
			email
		);
		oAuth2Repository.save(oAuth2Entity);

		// JWT 토큰 발급
		String accessToken = jwtTokenProvider.create(
			member.getId(),
			member.getMobile(),
			member.getRole()
		);

		return OAuth2TokenResponse.of(accessToken, 3600000L);
	}

	// 소셜 계정 추가 연동
	@Transactional
	public OAuth2LinkResponse linkAccount(
		OAuth2LinkRequest request,
		OAuth2Provider provider,
		String providerId,
		String email
	) {
		// 번호로 기존 멤버 확인
		MemberEntity member = memberRepository.findByMobile(request.mobile())
			.orElseThrow(() -> new BusinessException("등록되지 않은 휴대폰 번호입니다."));

		// 이미 연동된 소셜인지 확인
		if (oAuth2Repository.existsByMemberAndProvider(member, provider)) {
			throw new BusinessException("이미 연동된 소셜 계정입니다.");
		}

		// 소셜 연동
		OAuth2Entity oAuth2Entity = OAuth2Entity.create(
			member,
			provider,
			providerId,
			email
		);
		oAuth2Repository.save(oAuth2Entity);

		return OAuth2LinkResponse.of(provider, email);
	}

	// 로그인
	public OAuth2TokenResponse login(
		OAuth2Provider provider,
		String providerId
	) {
		// 소셜로 멤버 확인
		OAuth2Entity oAuth2Entity = oAuth2Repository
			.findByProviderAndProviderId(provider, providerId)
			.orElseThrow(() -> new BusinessException("등록되지 않은 소셜 계정입니다."));

		MemberEntity member = oAuth2Entity.getMember();

		// JWT 토큰 발급
		String accessToken = jwtTokenProvider.create(
			member.getId(),
			member.getMobile(),
			member.getRole()
		);

		return OAuth2TokenResponse.of(accessToken, 3600000L);
	}

	// 번호로 기존 멤버 확인
	public boolean existsByMobile(String mobile) {
		return memberRepository.existsByMobile(mobile);
	}

	// 연동 해제
	@Transactional
	public void unlinkOAuth2Account(
		UUID memberId,
		OAuth2Provider provider,
		String providerId,
		String accessToken
	) {
		// 멤버 확인
		MemberEntity member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException("멤버를 찾을 수 없습니다."));

		// 소셜 연동 조회
		OAuth2Entity oAuth2Entity = oAuth2Repository
			.findByProviderAndProviderId(provider, providerId)
			.orElseThrow(() -> new BusinessException("연동된 소셜 계정을 찾을 수 없습니다."));

		// 본인 계정인지 확인
		if (!oAuth2Entity.getMember().getId().equals(member.getId())) {
			throw new BusinessException("본인 계정이 아닙니다.");
		}

		// 소셜 연동 최소 개수
		long oAuth2Count = oAuth2Repository.countByMember(member);
		if (oAuth2Count <= 1) {
			throw new BusinessException("최소 한 개의 소셜 계정은 유지해야 합니다.");
		}

		// 소셜 연동 해제
		try {
			oAuth2UserUnlinkManager.unlink(provider, accessToken);
			log.info("연동 해제 성공 : Provider: {}, MemberId: {}", provider, memberId);
		} catch (Exception e) {
			log.error("연동 해제 실패 Provider: {}", e.getMessage());
		}

		oAuth2Repository.delete(oAuth2Entity);
		log.info("연동 해제 완료 : Provider: {}, MemberId: {}", provider, memberId);
	}

	public List<OAuth2AccountResponse> getLinkedOAuth2Accounts(UUID memberId) {
		MemberEntity member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException("멤버를 찾을 수 없습니다."));
		List<OAuth2Entity> linkedAccounts = oAuth2Repository.findALlByMember(member);

		return linkedAccounts.stream().map(this::mapToOAuth2AccountResponse).collect(Collectors.toList());
	}

	private OAuth2AccountResponse mapToOAuth2AccountResponse(OAuth2Entity entity) {
		return new OAuth2AccountResponse(
			entity.getProvider(),
			entity.getEmail(),
			entity.getCreatedAt()
		);
	}
}
