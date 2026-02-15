package com.ballx.repository.social;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballx.constants.ProviderType;
import com.ballx.domain.entity.social.SocialProviderEntity;
import com.ballx.domain.entity.user.MemberEntity;

@Repository
public interface SocialRepository extends JpaRepository<SocialProviderEntity, UUID> {

	boolean existsByMemberAndProvider(MemberEntity member, ProviderType provider);

	Optional<SocialProviderEntity> findByProviderAndProviderId(ProviderType provider, String providerId);

	List<SocialProviderEntity> findALlByMember(MemberEntity member);

	long countByMember(MemberEntity member);
}
