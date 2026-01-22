package com.ballx.domain.entity.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@MappedSuperclass
public abstract class ModificationTimestampEntity extends CreationTimestampEntity {

	@LastModifiedDate
	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private @Getter Instant updatedAt;

}
