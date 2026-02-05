package com.ballx.repository.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballx.domain.entity.user.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
	Optional<MemberEntity> findByMobile(String Mobile);

	boolean existsByMobile(String Mobile);

}
