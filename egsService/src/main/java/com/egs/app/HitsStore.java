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
			throw new CsServiceException(404L, "Cannot search session hits", "userId is null or empty");
		}
		
		if (null == sessionDateTimeAsString || sessionDateTimeAsString.isEmpty()) {
			throw new CsServiceException(404L, "Cannot search session hits", "sessionDate is null or empty");
		}

		if (null == hitCategoryAsString || hitCategoryAsString.isEmpty()) {
			throw new CsServiceException(404L, "Cannot search session hits", "hitCategory is null or empty");
		}

		if (null == clubTypeAsString || clubTypeAsString.isEmpty()) {
			throw new CsServiceException(404L, "Cannot search session hits", "clubType is null or empty");
		}

		Long userId = extractLong(userIdAsString);
		LocalDateTime sessionDateTime = LocalDateTime.parse(sessionDateTimeAsString);
		HitCategory hitCategory = HitCategory.valueOf(hitCategoryAsString);
		ClubType clubType = ClubType.valueOf(clubTypeAsString);

		HitsEntity hitsEntity = getHitsByData(userId, sessionDateTime, hitCategory, clubType);
		if (null == hitsEntity) {
			throw new CsServiceException(404L, "Hits not found", "Unknown userId, sessionDate, hitCategory and clubType");
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
			throw new CsServiceException(404L, "Cannot search session hits", "userId is null or empty");
		}
		
		if (null == sessionDateTimeAsString || sessionDateTimeAsString.isEmpty()) {
			throw new CsServiceException(404L, "Cannot search session hits", "sessionDate is null or empty");
		}

		LocalDateTime sessionDateTime = LocalDateTime.parse(sessionDateTimeAsString);
		Long userId = extractLong(userIdAsString);

		List<HitsEntity> hitsEntitys = getSessionHitsList(userId, sessionDateTime);
		if (null == hitsEntitys) {
			throw new CsServiceException(404L, "Hits not found", "No match for userId, sessionDate");
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
		if (null == hitsUpdateData.getUserId() || null == hitsUpdateData.get_id()) {
			throw new CsServiceException(401L, "Cannot update hits entry", "userId is empty");
		}
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, hitsUpdateData.getUserId(), rights);

		HitsEntity hitsToUpdate = getHits(hitsUpdateData.get_id());
		if (null == hitsToUpdate) {
			throw new CsServiceException(404L, "Cannot update hits entry", "No matching entity found");
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

		if (null == hitsContainer.getUserId() || null == hitsContainer.getSessionDateTime() || null == hitsContainer.getHitCategory()
				|| null == hitsContainer.getClubType()) {
			throw new CsServiceException(401L, "Hits entry not created", "userId or sessionId is empty");
		}

		// does the Configuration already exists?
		//
		if (null != getHitsByData(hitsContainer.getUserId(), hitsContainer.getSessionDateTime(), hitsContainer.getHitCategory(), hitsContainer.getClubType())) {
			throw new CsServiceException(409L, "Hits entry not created",
					"entry already exists, try update method");
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
			throw new CsServiceException(401L, "Cannot update hits", "Hits not found by id");
		}
		storedHits.updateFrom(hitsUpdateData);
		storedHits = hitModel.save(storedHits);
		if (null == storedHits) {
			throw new CsServiceException(401L, "Hits not updated",
					"Unknown reason but hits entry was not saved");
		}
		return storedHits;
	}

	public void dropConfigurationSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String configurationIdAsString = requestParams.get("configurationId");
		if (null == configurationIdAsString) {
			throw new CsServiceException(404L, "Cannot drop hits", "Hits id is empty");
		}
		Long configurationId = extractLong(configurationIdAsString);

		HitsEntity hitsEntity = getHits(configurationId);
		if (null == hitsEntity) {
			throw new CsServiceException(404L, "Hits not found", "No match for _id");
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
			throw new CsServiceException(404L, "Hits not deleted",
					"Unknown reason but hits entry was not deleted");
		}
	}

	private Long extractLong(String longString) throws CsServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new CsServiceException(500L, "Extraction Long from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}
}
