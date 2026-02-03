package com.ballx.repository.oauth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballx.constants.OAuth2Provider;
import com.ballx.domain.entity.oauth.OAuth2Entity;
import com.ballx.domain.entity.user.MemberEntity;

@Repository
public interface OAuth2Repository extends JpaRepository<OAuth2Entity, UUID> {

	boolean existsByMemberAndProvider(MemberEntity member, OAuth2Provider provider);

	Optional<OAuth2Entity> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
}
