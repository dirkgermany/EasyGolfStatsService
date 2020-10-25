package com.egs.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.egs.app.PermissionCheck;
import com.egs.app.model.HitModel;
import com.egs.app.model.entity.HitsEntity;
import com.egs.app.rest.message.HitsWriteRequest;
import com.egs.app.types.ClubType;
import com.egs.app.types.HitCategory;
import com.egs.app.types.RestError;
import com.egs.exception.CsServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class HitsStore {

	@Autowired
	private HitModel hitModel;

	public long count() {
		return hitModel.count();
	}
	
	public HitsEntity getHitsSafe(Map<String, String> requestParams, Map<String, String> headers) throws CsServiceException {
						
		String requestorUserIdAsString = requestParams.get("requestorUserId");
		if (null == requestorUserIdAsString) {
			requestorUserIdAsString = headers.get("requestoruserid");
		}
		String rights = requestParams.get("rights");
		if (null == rights) {
			rights = headers.get("rights");
		}

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String userIdAsString = requestParams.get("userId");
		String sessionDateTimeAsString = requestParams.get("sessionDateTime");
		String hitCategoryAsString = requestParams.get("hitCategory");
		String clubTypeAsString = requestParams.get("clubType");
		
		if (null == userIdAsString || userIdAsString.isEmpty()) {
			throw new CsServiceException(404L, RestError.EMPTY_USER_ID.name(),"Cannot search session hits");
		}
		
		if (null == sessionDateTimeAsString || sessionDateTimeAsString.isEmpty()) {
			throw new CsServiceException(404L,  RestError.EMPTY_SESSION_ID.name(),"Cannot search session hits");
		}

		if (null == hitCategoryAsString || hitCategoryAsString.isEmpty()) {
			throw new CsServiceException(404L, RestError.EMPTY_HIT_CATEGORY.name(),"Cannot search session hits");
		}

		if (null == clubTypeAsString || clubTypeAsString.isEmpty()) {
			throw new CsServiceException(404L,  RestError.EMPTY_CLUB_TYPE.name(),"Cannot search session hits");
		}

		Long userId = extractLong(userIdAsString);
		LocalDateTime sessionDateTime = LocalDateTime.parse(sessionDateTimeAsString);
		HitCategory hitCategory = HitCategory.valueOf(hitCategoryAsString);
		ClubType clubType = ClubType.valueOf(clubTypeAsString);

		HitsEntity hitsEntity = getHitsByData(userId, sessionDateTime, hitCategory, clubType);
		if (null == hitsEntity) {
			throw new CsServiceException(404L, RestError.ENTITY_NOT_FOUND.name(), "Hits not found");
		}
		PermissionCheck.isReadPermissionSet(requestorUserId, hitsEntity.getUserId(), rights);
		return hitsEntity;
				
	}

	public List<HitsEntity> listSessionHitsSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = requestParams.get("requestorUserId");
		if (null == requestorUserIdAsString) {
			requestorUserIdAsString = headers.get("requestoruserid");
		}
		String rights = requestParams.get("rights");
		if (null == rights) {
			rights = headers.get("rights");
		}

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String userIdAsString = requestParams.get("userId");
		String sessionDateTimeAsString = requestParams.get("sessionDateTime");
		
		if (null == userIdAsString || userIdAsString.isEmpty()) {
			throw new CsServiceException(404L, RestError.EMPTY_USER_ID.name(), "Cannot search session hits");
		}
		
		if (null == sessionDateTimeAsString || sessionDateTimeAsString.isEmpty()) {
			throw new CsServiceException(404L, RestError.EMPTY_SESSION_ID.name(), "Cannot search session hits");
		}

		LocalDateTime sessionDateTime = LocalDateTime.parse(sessionDateTimeAsString);
		Long userId = extractLong(userIdAsString);

		List<HitsEntity> hitsEntitys = getSessionHitsList(userId, sessionDateTime);
		if (null == hitsEntitys) {
			throw new CsServiceException(404L, RestError.ENTITY_NOT_FOUND.name(), "Hits not found");
		}
		
		List<HitsEntity> returnList = new ArrayList<>();
		Iterator<HitsEntity> it = hitsEntitys.iterator();
		while (it.hasNext()) {
			HitsEntity hits = it.next();
			try {
				PermissionCheck.isReadPermissionSet(requestorUserId, hits.getUserId(), rights);
				returnList.add(hits);
			} catch (CsServiceException cse) {
				// User may not read this configuration value
				// try next entry
			}
		}

		return returnList;
	}

	public List<HitsEntity> listAllHitsSafe(Map<String, String> headers) throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		ArrayList<HitsEntity> hitsEntitys = new ArrayList<>();
		hitModel.findAll().forEach(hits -> {
			hitsEntitys.add(hits);
		});

		return hitsEntitys;
	}

	public HitsEntity createHitsSafe(HitsWriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getHitsEntity(), HitsEntity.class, "");

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, requestBody.getHitsEntity().getUserId(), rights);

		return createHits(requestBody.getHitsEntity());
	}

	public HitsEntity updateHitsSafe(HitsWriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getHitsEntity(), HitsEntity.class, "");

		HitsEntity hitsUpdateData = requestBody.getHitsEntity();
		if (null == hitsUpdateData.getUserId() && null == hitsUpdateData.get_id()) {
			throw new CsServiceException(401L, RestError.MISSED_VALUE.name(), "Cannot update hits entry");
		}
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, hitsUpdateData.getUserId(), rights);

		HitsEntity hitsToUpdate = getHits(hitsUpdateData.get_id());
		if (null == hitsToUpdate) {
			throw new CsServiceException(404L, RestError.ENTITY_NOT_FOUND.name(), "Cannot update hits entry");
		}

		hitsToUpdate.updateFrom(hitsUpdateData);
		return updateHits(hitsToUpdate);
	}

	private List<HitsEntity> getSessionHitsList(Long userId, LocalDateTime sessionDateTime) throws CsServiceException {
		ArrayList<HitsEntity> hitsEntitys = new ArrayList<>();
		hitModel.findList(userId, sessionDateTime).forEach(hits -> {
			hitsEntitys.add(hits);
		});	


		if (hitsEntitys.size() < 1) {
			return null;
		}

		return hitsEntitys;
	}

	private HitsEntity createHits(HitsEntity hitsContainer) throws CsServiceException {

		if (null == hitsContainer.getUserId()) {
			throw new CsServiceException(401L, RestError.EMPTY_USER_ID.name(), "Entity not created");
		}
		
		if (null == hitsContainer.getSessionDateTime()) {
			throw new CsServiceException(401L, RestError.EMPTY_SESSION_ID.name(), "Entity not created");

		}
		
		if (null == hitsContainer.getHitCategory()) {
			throw new CsServiceException(401L, RestError.EMPTY_HIT_CATEGORY.name(), "Entity not created");
		}

		if (null == hitsContainer.getClubType()) {
			throw new CsServiceException(401L, RestError.EMPTY_CLUB_TYPE.name(), "Entity not created");
		}

		// does the entity already exists?
		//
		if (null != getHitsByData(hitsContainer.getUserId(), hitsContainer.getSessionDateTime(), hitsContainer.getHitCategory(), hitsContainer.getClubType())) {
			throw new CsServiceException(409L, RestError.CREATE_DUPLICATE_KEY.name(), "Entity not created");
		}

		try {
			HitsEntity hitsEntity = hitModel.save(hitsContainer);
			return hitsEntity;
		} catch (Exception ex) {
			return null;
		}
	}

	private HitsEntity updateHits(HitsEntity hitsUpdateData) throws CsServiceException {

		HitsEntity storedHits = getHits(hitsUpdateData.get_id());
		if (null == storedHits) {
			throw new CsServiceException(401L, RestError.ENTITY_NOT_FOUND_BY_ID.name(), "Cannot update hits");
		}
		storedHits.updateFrom(hitsUpdateData);
		storedHits = hitModel.save(storedHits);
		if (null == storedHits) {
			throw new CsServiceException(401L, RestError.UNKNOWN.name(), "Cannot update hits");
		}
		return storedHits;
	}

	public void dropHitsSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String hitsIdAsString = requestParams.get("_Id");
		if (null == hitsIdAsString) {
			throw new CsServiceException(404L, RestError.EMPTY_ENTITY_ID.name(), "Cannot drop hits");
		}
		Long hitsId = extractLong(hitsIdAsString);

		HitsEntity hitsEntity = getHits(hitsId);
		if (null == hitsEntity) {
			throw new CsServiceException(404L, RestError.ENTITY_NOT_FOUND_BY_ID.name(), "Cannot drop hits");
		}

		PermissionCheck.isDeletePermissionSet(requestorUserId, hitsEntity.getUserId(), rights);
		dropHits(hitsEntity.get_id());
	}
	
	private HitsEntity getHitsByData(Long userId, LocalDateTime sessionDateTime, HitCategory hitCategory, ClubType clubType) {
		return hitModel.find(userId, sessionDateTime, hitCategory, clubType);
	}

	public HitsEntity getHits(Long _id) {
		Optional<HitsEntity> optionalConfiguration = hitModel.findById(_id);
		if (null != optionalConfiguration && optionalConfiguration.isPresent()) {
			HitsEntity hitsEntity = optionalConfiguration.get();
			return hitsEntity;
		}
		return null;
	}

	private void dropHits(Long _id) throws CsServiceException {
		hitModel.deleteById(_id);
		HitsEntity deletedConfiguration = getHits(_id);
		if (null != deletedConfiguration) {
			throw new CsServiceException(404L, RestError.UNKNOWN.name(), "Hits not deleted");
		}
	}

	private Long extractLong(String longString) throws CsServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new CsServiceException(500L, RestError.MISSED_VALUE.name(), "Extraction Long from String failed");
		}
	}
}
