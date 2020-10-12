package com.egs.app.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.egs.app.model.entity.HitsEntity;

@Transactional
public interface HitModel extends Repository<HitsEntity, Long>, CrudRepository<HitsEntity, Long> {

//	Configuration findByConfKey(String confKey);

	// KEY
	@Query("SELECT config FROM ConfigEntity config where config.confKey = :confKey ")
	List<HitsEntity> findList(@Param("confKey") String confKey);
		
	// USER KEY
	@Query("SELECT config FROM ConfigEntity config where config.userId = :userId AND config.confKey = :confKey ")
	List<HitsEntity> findList(@Param("userId") Long userId, @Param("confKey") String confKey);
	
	// KEY INDEX
	@Query("SELECT config FROM ConfigEntity config where config.confKey = :confKey AND config.listIndex = :listIndex")
	HitsEntity find(@Param("confKey") String confKey, @Param("listIndex") Integer listIndex);
	
	// USER KEY INDEX
	@Query("SELECT config FROM ConfigEntity config where config.userId = :userId "
			+ "AND config.confKey = :confKey AND config.listIndex = :listIndex")
	HitsEntity find(@Param("userId") Long userId, @Param("confKey") String confKey, @Param("listIndex") Integer listIndex);
		
//	@Modifying
//	@Transactional
//	@Query("Update Configuration config set "
//	        + "config.value = :#{#config.value}, "
//	        + "config.listIndex = :#{#config.listIndex}, "
//			+ "config.hidden = :#{#config.hidden}, "
//			+ "Where config.confKey = :#{#config.confKey} "
//			+ "AND   config.userId = :#{#config.userId} "
//			+ "AND   config.listIndex = :#{#config.listIndex}")
//	Integer updateConfiguration(@Param("config") Configuration configuration);
}
