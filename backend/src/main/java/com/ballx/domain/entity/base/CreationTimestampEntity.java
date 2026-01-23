package com.ballx.domain.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreationTimestampEntity extends BaseUuidEntity {

	@CreatedDate
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private @Getter Instant createdAt;
}
