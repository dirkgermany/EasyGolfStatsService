package com.egs.app.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "Configuration", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "userId", "confKey", "listIndex" }) }, indexes = {
				@Index(name = "idx_config_primary", columnList = "userId, confKey, listIndex"),
				@Index(name = "idx_config_secondary", columnList = "confKey, listIndex") })

public class ConfigEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long _id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String confKey;

	@Column(nullable = false)
	private String value;

	@Column(nullable = true)
	@Type(type="yes_no")
	private Boolean hidden = Boolean.FALSE;

	@Column(nullable = true)
	private Integer listIndex;

	public ConfigEntity() {
	}

	public ConfigEntity(Long userId, String confKey, String value, Integer listIndex, Boolean hidden) {
		this.userId = userId;
		this.confKey = confKey;
		this.value = value;
		this.listIndex = listIndex;
		this.hidden = hidden;
	}

	public Long get_id() {
		return _id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getConfKey() {
		return confKey;
	}

	public void setConfKey(String confKey) {
		this.confKey = confKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Integer getListIndex() {
		return listIndex;
	}

	public void setListIndex(Integer listIndex) {
		this.listIndex = listIndex;
	}

	/**
	 * Updates Entity values.
	 */
	public void updateFrom(ConfigEntity updateConfiguration) {
		if (null == updateConfiguration) {
			return;
		}
		this.hidden = updateConfiguration.getHidden();
		this.listIndex = updateConfiguration.getListIndex();
		this.value = updateConfiguration.getValue();
	}
}
