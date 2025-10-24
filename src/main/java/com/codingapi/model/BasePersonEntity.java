package com.codingapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BasePersonEntity {

	@Column(name = "is_active", nullable = false)
	private boolean isActive = true;

	@Version
	private Integer version;

	public String getEntityType() {
		return this.getClass().getSimpleName();
	}
}