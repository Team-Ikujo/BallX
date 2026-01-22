package com.ballx.domain.entity.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@MappedSuperclass
public abstract class BaseUuidEntity implements Persistable<UUID> {
	@Id
	@UuidGenerator
	@Column(name = "id", nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private @Getter UUID id;

	@Override
	public boolean isNew() {
		return id == null;
	}

}
