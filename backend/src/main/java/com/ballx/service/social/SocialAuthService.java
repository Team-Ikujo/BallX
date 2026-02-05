package com.ballx.service.social;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballx.constants.ProviderType;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.dto.response.social.SocialCallbackResponse;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.ballx.exception.CustomException;
import com.ballx.infra.social.SocialAuthProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SocialAuthService {

	private final Map<String, SocialAuthProvider> providerMap;

	public SocialAuthService(List<SocialAuthProvider> providers) {
		this.providerMap = providers.stream()
			.collect(Collectors.toMap(
					SocialAuthProvider::getProviderName,
					Function.identity()
				)
			);
	}

	public SocialCallbackResponse getCode(ProviderType providerType, String code) {
		return SocialCallbackResponse.of(providerType, code);
	}

	public SocialTokenInfoResponse getAccessToken(ProviderType providerType, String code) {
		SocialAuthProvider provider = providerMap.get(providerType.getProviderId());

		if (provider == null) {
			throw new CustomException(ErrorCode.SOCIAL_PROVIDER_NOT_SUPPORTED);
		}

		return provider.getAccessToken(code);
	}
}
