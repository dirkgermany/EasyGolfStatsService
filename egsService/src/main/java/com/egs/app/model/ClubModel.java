package com.egs.app.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.egs.app.model.entity.ClubEntity;
import com.egs.app.types.ClubType;

@Transactional
public interface ClubModel extends Repository<ClubEntity, Long>, CrudRepository<ClubEntity, Long> {

	// USER
	@Query("SELECT club FROM ClubEntity club where club.userId = :userId ")
	List<ClubEntity> findList(@Param("userId") Long userId);
	
	// USER CLUBTYPE
	@Query("SELECT club FROM ClubEntity club where club.userId = :userId "
			+ "AND club.clubType = :clubType")
	ClubEntity find(@Param("userId") Long userId, @Param("clubType") ClubType clubType);

	// USER CLUBNAME
	@Query("SELECT club FROM ClubEntity club where club.userId = :userId "
			+ "AND club.clubName = :clubName")
	ClubEntity find(@Param("userId") Long userId, @Param("clubName") String clubName);

}
