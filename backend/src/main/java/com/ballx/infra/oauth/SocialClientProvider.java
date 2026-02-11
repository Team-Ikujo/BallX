package com.ballx.infra.oauth;

import com.ballx.constants.ProviderType;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialClientProvider {

	private final Map<ProviderType, SocialApiClient> clients;

	public SocialClientProvider(List<SocialApiClient> clientList) {
		this.clients = clientList.stream()
			.collect(Collectors.toMap(
				SocialApiClient::getProviderType,
				Function.identity()
			));
	}

	public SocialApiClient getClient(ProviderType provider) {
		SocialApiClient client = clients.get(provider);
		if (client == null) {
			throw new CustomException(ErrorCode.SOCIAL_PROVIDER_NOT_SUPPORTED);
		}
		return client;
	}
}
