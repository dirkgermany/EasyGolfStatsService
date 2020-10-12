package com.egs.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.egs.app.model.ClubModel;
import com.egs.app.model.entity.ClubEntity;
import com.egs.app.rest.message.WriteRequest;
import com.egs.app.types.ClubType;
import com.egs.exception.CsServiceException;

/** * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class ClubStore {

	@Autowired
	private ClubModel clubModel;

	public long count() {
		return clubModel.count();
	}

	public List<ClubEntity> listClubsSafe(Map<String, String> requestParams, Map<String, String> headers)
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

		Long userId = null;
		if (null != userIdAsString) {
			userId = extractLong(userIdAsString);
		}

		List<ClubEntity> clubEntitys = getClubs(requestorUserId, userId);
		if (null == clubEntitys) {
			throw new CsServiceException(404L, "Clubs for user not found", "No match for userId");
		}

		return clubEntitys;
	}

	public List<ClubEntity> listAllConfigurationsSafe(Map<String, String> headers) throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		ArrayList<ClubEntity> clubEntitys = new ArrayList<>();
		clubModel.findAll().forEach(club -> {
			clubEntitys.add(club);
		});

		return clubEntitys;
	}

	public ClubEntity createClubSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getClubEntity(), ClubEntity.class, "");

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, requestBody.getClubEntity().getUserId(), rights);

		return createClub(requestBody.getClubEntity());
	}

	public ClubEntity updateClubSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getClubEntity(), ClubEntity.class, "");

		ClubEntity clubUpdateData = requestBody.getClubEntity();
		if (null == clubUpdateData.getUserId()) {
			throw new CsServiceException(401L, "Cannot update club", "userId is empty");
		}
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, clubUpdateData.getUserId(), rights);

		ClubEntity entityToUpdate = getClub(clubUpdateData.getUserId(), clubUpdateData.getClubType());
		if (null == entityToUpdate) {
			throw new CsServiceException(404L, "Cannot update club", "No matching entity found");
		}
		entityToUpdate.updateFrom(clubUpdateData);
		return updateClub(entityToUpdate);
	}

	private List<ClubEntity> getClubs(Long requestorUserId, Long userId) throws CsServiceException {
		if (null == userId) {
			throw new CsServiceException(401L, "Cannot list clubs", "userId is empty");
		}

		// USER
		List<ClubEntity> clubList = clubModel.findList(userId);
			if (null == clubList) {
				throw new CsServiceException(401L, "Club list is empty", "no club found for user");
			}

		return clubList;
	}
	
	/**
	 * Creates a user. Checks if the userName still exists (must be distinct)
	 * 
	 * @param userName
	 * @param password
	 * @param givenName
	 * @param lastName
	 * @return
	 */
	private ClubEntity createClub(ClubEntity clubContainer) throws CsServiceException {

		if (null == clubContainer.getUserId() || null == clubContainer.getClubType()) {
			throw new CsServiceException(409L, "Club not created", "userId or clubType is empty");
		}

		// does the Club already exists?
		//
		if (null != getClub(clubContainer.getUserId(), clubContainer.getClubType())) {
			throw new CsServiceException(409L, "Club not created",
					"Club already exists, try update method");
		}

		try {
			ClubEntity hitsEntity = clubModel.save(clubContainer);
			return hitsEntity;
		} catch (Exception ex) {
			return null;
		}
	}

	private ClubEntity updateClub(ClubEntity clubUpdateData) throws CsServiceException {

		ClubEntity storedClub = getClub(clubUpdateData.get_id());
		if (null == storedClub) {
			throw new CsServiceException(401L, "Cannot update club", "Club not found by parameters");
		}
		storedClub.updateFrom(clubUpdateData);
		storedClub = clubModel.save(storedClub);
		if (null == storedClub) {
			throw new CsServiceException(401L, "Club not updated", "Unknown reason but club was not saved");
		}
		return storedClub;
	}

	public void dropClubSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String clubIdAsString = requestParams.get("clubId");
		if (null == clubIdAsString) {
			throw new CsServiceException(404L, "Cannot drop club", "Club id is empty");
		}
		Long clubId = extractLong(clubIdAsString);

		ClubEntity clubEntity = getClub(clubId);
		if (null == clubEntity) {
			throw new CsServiceException(404L, "Club not found", "No match for userId or clubId");
		}

		PermissionCheck.isDeletePermissionSet(requestorUserId, clubEntity.getUserId(), rights);
		dropClub(clubEntity.get_id());
	}

	public ClubEntity getClub(Long _id) {
		Optional<ClubEntity> optionalConfiguration = clubModel.findById(_id);
		if (null != optionalConfiguration && optionalConfiguration.isPresent()) {
			ClubEntity hitsEntity = optionalConfiguration.get();
			return hitsEntity;
		}
		return null;
	}
	
	public ClubEntity getClub(Long userId, ClubType clubType) {
		return clubModel.find(userId, clubType);
	}

	private void dropClub(Long _id) throws CsServiceException {
		clubModel.deleteById(_id);
		ClubEntity deletedClub = getClub(_id);
		if (null != deletedClub) {
			throw new CsServiceException(404L, "Club not deleted",
					"Unknown reason but configuration was not deleted");
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

	private Integer extractInteger(String integerString) throws CsServiceException {
		try {
			return Integer.valueOf(integerString);
		} catch (Exception e) {
			throw new CsServiceException(500L, "Extraction Integer from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}

	private Boolean extractBoolean(String booleanString) throws CsServiceException {
		try {
			return Boolean.valueOf(booleanString);
		} catch (Exception e) {
			throw new CsServiceException(500L, "Extraction Boolean from String failed",
					"Parameter is required but null or does not represent a Boolean value");
		}
	}
}
