package com.egs.app.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;

import com.egs.app.types.ClubType;

@Entity
@Component
@Table(name = "Clubs", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "userId", "clubType" }) }, indexes = {
				@Index(name = "idx_config_primary", columnList = "userId, clubType")})

public class ClubEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long _id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ClubType clubType;

	@Column(nullable = false)
	private String clubName;

	public ClubEntity() {
	}

	public ClubEntity(Long userId, ClubType clubType, String clubName) {
		this._id = _id;
		this.userId = userId;
		this.clubType = clubType;
		this.clubName = clubName;
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

	public ClubType getClubType() {
		return clubType;
	}

	public void setClubType(ClubType clubType) {
		this.clubType = clubType;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	/**
	 * Updates Entity values.
	 */
	public void updateFrom(ClubEntity updateClub) {
		if (null == updateClub) {
			return;
		}
		this.clubName = updateClub.getClubName();
		this.clubType = updateClub.getClubType();
	}
}
