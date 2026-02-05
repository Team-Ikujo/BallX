package com.ballx.domain.entity.oauth;

import static lombok.AccessLevel.*;

import org.springframework.util.StringUtils;

import com.ballx.constants.ProviderType;
import com.ballx.domain.entity.base.ModificationTimestampEntity;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.validation.Preconditions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
	name = "oauth2_entity",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_member_provider",
			columnNames = {"member_id", "provider"}
		)})
@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SocialProviderEntity extends ModificationTimestampEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private MemberEntity member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProviderType provider;

	@Column(nullable = false)
	private String providerId;

	@Column(nullable = false)
	private String email;

	private SocialProviderEntity(MemberEntity member, ProviderType provider, String providerId, String email) {
		this.member = member;
		this.provider = provider;
		this.providerId = providerId;
		this.email = email;
	}

	public static SocialProviderEntity create(MemberEntity member, ProviderType provider, String providerId,
		String email) {
		validate(member, provider, providerId, email);
		return new SocialProviderEntity(member, provider, providerId, email);
	}

	private static void validate(MemberEntity member, ProviderType provider, String providerId, String email) {
		Preconditions.domainValidate(
			member != null, "멤버는 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			provider != null, "제공자는 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			StringUtils.hasText(providerId), "제공자 번호는 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			StringUtils.hasText(email), "이메일은 비어 있을 수 없습니다."
		);

	}
}


