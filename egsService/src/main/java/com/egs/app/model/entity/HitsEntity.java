package com.egs.app.model.entity;

import java.time.LocalDateTime;

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
import com.egs.app.types.HitCategory;

@Entity
@Component
@Table(name = "Hits", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "userId", "sessionDateTime", "hitCategory", "clubType" }) }, indexes = {
				@Index(name = "idx_hits_primary", columnList = "userId, sessionDateTime, hitCategory, clubType"),
				@Index(name = "idx_hits_secondary", columnList = "userId, sessionDateTime") })

public class HitsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long _id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private LocalDateTime sessionDateTime;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private HitCategory hitCategory;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ClubType clubType;

	@Column(nullable = false)
	private Integer hitCountGood;

	@Column(nullable = false)
	private Integer hitCountNeutral;

	@Column(nullable = false)
	private Integer hitCountBad;

	public HitsEntity() {
	}

	public HitsEntity(Long userId, LocalDateTime sessionDateTime, HitCategory hitCategory, ClubType clubType,
			Integer hitCountGood, Integer hitCountNeutral, Integer hitCountBad) {
		this.userId = userId;
		this.sessionDateTime = sessionDateTime;
		this.hitCategory = hitCategory;
		this.clubType = clubType;
		this.hitCountGood = hitCountGood;
		this.hitCountNeutral = hitCountNeutral;
		this.hitCountBad = hitCountBad;
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

	public HitCategory getHitCategory() {
		return this.hitCategory;
	}

	public void setHitCategory(HitCategory hitCategory) {
		this.hitCategory = hitCategory;
	}

	public ClubType getClubType() {
		return this.clubType;
	}

	public void setClubType(ClubType clubType) {
		this.clubType = clubType;
	}

	public LocalDateTime getSessionDateTime() {
		return this.sessionDateTime;
	}

	public void setSessionDateTime(LocalDateTime sessionDateTime) {
		this.sessionDateTime = sessionDateTime;
	}

	public Integer getHitCountGood() {
		return this.hitCountGood;
	}

	public void setHitCountGood(Integer hitCountGood) {
		this.hitCountGood = hitCountGood;
	}

	public Integer getHitCountNeutral() {
		return this.hitCountNeutral;
	}

	public void setHitCountNeutral(Integer hitCountNeutral) {
		this.hitCountNeutral = hitCountNeutral;
	}

	public Integer getHitCountBad() {
		return this.hitCountBad;
	}

	public void setHitCountBad(Integer hitCountBad) {
		this.hitCountBad = hitCountBad;
	}

	/**
	 * Updates Entity values.
	 */
	public void updateFrom(HitsEntity updateHits) {
		if (null == updateHits) {
			return;
		}
		setHitCategory(updateHits.getHitCategory());
		setClubType(updateHits.getClubType());
		setSessionDateTime(updateHits.getSessionDateTime());
		setHitCountGood(updateHits.getHitCountGood());
		setHitCountNeutral(updateHits.getHitCountNeutral());
		setHitCountBad(updateHits.getHitCountBad());
	}
}
