package com.egs.app.model.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
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
		@UniqueConstraint(columnNames = { "userId", "sessionDate", "hitCategory", "clubType" }) }, indexes = {
				@Index(name = "idx_hits_primary", columnList = "userId, sessionDate, hitCategory, clubType"),
				@Index(name = "idx_hits_secondary", columnList = "userId, sessionDate") })

public class HitsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long _id;

	@Column(nullable = false)
	private Long userId;
	
	@Column(nullable = false)
	private LocalDate sessionDate;
	
	@Column(nullable = false) 
	private HitCategory hitCategory;
	
	@Column(nullable = false)
	private ClubType clubType;
	
	@Column(nullable = false)
	private Integer hitCountGood;
	
	@Column(nullable = false)
	private Integer hitCountNeutral;
	
	@Column(nullable = false)
	private Integer hitCountBad;
	

	public HitsEntity() {
	}

	public HitsEntity(Long userId, LocalDate sessionDate, HitCategory hitCategory, ClubType clubType, Integer hitCountGood, Integer hitCountNeutral, Integer hitCountBad) {
		this.userId = userId;
		this.sessionDate = sessionDate;
		this.hitCategory = hitCategory;
		this.clubType = clubType;
		this.hitCountGood = hitCountBad;
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

	public HitCategory getHitCategory () {
		return this.hitCategory;
	}
	
	public void setHitCategory (HitCategory hitCategory) {
		this.hitCategory = hitCategory;
	}
	
	public ClubType getClubType () {
		return this.clubType;
	}
	
	public void setClubType (ClubType clubType) {
		this.clubType = clubType;
	}
	
	public LocalDate getSessionDate () {
		return this.sessionDate;
	}
	
	public void setSessionDate (LocalDate sessionDate) {
		this.sessionDate = sessionDate;
	}
	
	public Integer getHitCountGood () {
		return this.hitCountGood;
	}
	
	public void setHitCountGood (Integer hitCountGood) {
		this.hitCountGood = hitCountGood;
	}

	public Integer getHitCountNeutral () {
		return this.hitCountNeutral;
	}
	
	public void setHitCountNeutral (Integer hitCountNeutral) {
		this.hitCountNeutral = hitCountNeutral;
	}

	public Integer getHitCountBad () {
		return this.hitCountBad;
	}
	
	public void setHitCountBad (Integer hitCountBad) {
		this.hitCountBad = hitCountBad;
	}

	/**
	 * Updates Entity values.
	 */
	public void updateFrom(HitsEntity updateHits) {
		if (null == updateHits) {
			return;
		}
		this.hitCategory = updateHits.getHitCategory();
		this.clubType = updateHits.getClubType();
		this.sessionDate = updateHits.getSessionDate();
		this.hitCountGood = updateHits.getHitCountGood();
		this.hitCountNeutral = updateHits.getHitCountNeutral();
		this.hitCountBad = updateHits.getHitCountBad();
	}
}
