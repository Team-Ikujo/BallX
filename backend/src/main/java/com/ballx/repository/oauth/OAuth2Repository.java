package com.ballx.repository.oauth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballx.constants.ProviderType;
import com.ballx.domain.entity.oauth.SocialProviderEntity;
import com.ballx.domain.entity.user.MemberEntity;

@Repository
public interface OAuth2Repository extends JpaRepository<SocialProviderEntity, UUID> {

	boolean existsByMemberAndProvider(MemberEntity member, ProviderType provider);

	Optional<SocialProviderEntity> findByProviderAndProviderId(ProviderType provider, String providerId);

	List<SocialProviderEntity> findALlByMember(MemberEntity member);

	long countByMember(MemberEntity member);
}
