package com.ballx.service.social;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.response.social.SocialCallbackResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SocialAuthService {

	public SocialCallbackResponse getCode(ProviderType providerType, String code) {
		return SocialCallbackResponse.of(providerType, code);
	}
}
