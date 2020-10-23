package com.egs.app.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.egs.app.model.entity.HitsEntity;
import com.egs.app.types.ClubType;
import com.egs.app.types.HitCategory;

@Transactional
public interface HitModel extends Repository<HitsEntity, Long>, CrudRepository<HitsEntity, Long> {
		
	@Query("SELECT hits FROM HitsEntity hits where hits.userId = :userId "
			+ "AND hits.sessionDateTime = :sessionDateTime "
			+ "AND hits.hitCategory     = :hitCategory "
			+ "AND hits.clubType        = :clubType ")
	HitsEntity find(@Param("userId") Long userId, @Param("sessionDateTime") LocalDateTime sessionDateTime, @Param("hitCategory") HitCategory hitCategory, @Param("clubType") ClubType clubType);

	
	@Query("SELECT hits FROM HitsEntity hits where hits.userId = :userId "
			+ "AND hits.sessionDateTime = :sessionDateTime")
	List<HitsEntity> findList(@Param("userId") Long userId, @Param("sessionDateTime") LocalDateTime sessionDateTime);
		
}
