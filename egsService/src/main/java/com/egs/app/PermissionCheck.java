package com.egs.app;

import com.egs.app.rest.message.RestRequest;
import com.egs.exception.CsServiceException;
import com.egs.exception.PermissionCheckException;

public class PermissionCheck {
	
	/**
	 * Checks preconditions
	 * @param request
	 * @param requestorUserId
	 * @param rights
	 * @throws CsServiceException
	 */
	public static void checkRequestedParams(RestRequest request, String requestorUserId, String rights)
			throws CsServiceException {
		if (null == request) {
			throw new CsServiceException(new Long(400), "Invalid Request", "Request is null.");
		}
		if (null == requestorUserId) {
			throw new CsServiceException(new Long(400), "Invalid Request",
					"requestorUserId is recommended but not set.");
		}
		if (null == rights || rights.isEmpty()) {
			throw new CsServiceException(new Long(400), "Invalid Request",
					"User rights are recommended but are null or empty.");
		}		
	}
	
	public static void checkRequestedParams(String requestorUserId, String rights)
			throws CsServiceException {
		if (null == requestorUserId) {
			throw new CsServiceException(new Long(400), "Invalid Request",
					"requestorUserId is recommended but not set.");
		}
		if (null == rights || rights.isEmpty()) {
			throw new CsServiceException(new Long(400), "Invalid Request",
					"User rights are recommended but are null or empty.");
		}
	}
	
	/**
	 * Checks if a required object is existing
	 * @param entity
	 * @param theClass
	 * @throws CsServiceException
	 */
	@SuppressWarnings("rawtypes")
	public static void checkRequestedEntity(Object entity, Class theClass, String additionalInformation) 
			throws CsServiceException {
		if (null == entity) {
			throw new CsServiceException(new Long(400), "Invalid Request", "Required object " + theClass.getSimpleName() + " is null." + "; " + additionalInformation);
		}
		if (!entity.getClass().getName().equals(theClass.getName())) {
			throw new CsServiceException(new Long(400), "Invalid Request", "Object " + entity.getClass().getSimpleName() + " is not instance of " + theClass.getSimpleName()+ "; " + additionalInformation);
		}
	}



	/**
	 * Checks if the WRITE permission is set. TRUE, when requestor and owner are the
	 * same and Owner Right for Writing is set. For Creation also the Owner Right
	 * should be prooved.
	 * 
	 * @param requestorUserId
	 * @param userId
	 * @param rights
	 * @return
	 */
	public static void isWritePermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (!isMarkerSet(requestorUserId, userId, rights, "W")) {
			throw new PermissionCheckException(new Long(403), "No Write Permission",
					"User has no permissions to write or update the dataset");
		}
	}

	/**
	 * Checks if any right to write is set
	 * 
	 * @param rights
	 * @return
	 */
	public static void isWritePermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (!rights.toUpperCase().contains("W")) {
			throw new PermissionCheckException(new Long(403), "No Write Permission in general",
					"User has no permissions to write or update a dataset in general");
		}
	}

	public static void isReadPermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (!isMarkerSet(requestorUserId, userId, rights, "R")) {
			throw new PermissionCheckException(new Long(403), "No Read Permission",
					"User has no permissions to read the requested dataset");
		}
	}

	public static void isReadPermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (! rights.toUpperCase().contains("R")) {
			throw new PermissionCheckException(new Long(403), "No Read Permission in general",
					"User has no permissions to read a dataset in general");
		}
	}

	public static void isDeletePermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (! isMarkerSet(requestorUserId, userId, rights, "D")) {
			throw new PermissionCheckException(new Long(403), "No Delete Permission",
					"User has no permissions to delete the requested dataset");
		}
	}

	public static void isDeletePermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (!rights.toUpperCase().contains("W")) {
			throw new PermissionCheckException(new Long(403), "No Delete Permission in general",
					"User has no permissions to delete a dataset in general");
		}
	}

	private static Boolean rightCheck(String rights) {
		return rights.contains("+");
	}

	private static Boolean isMarkerSet(Long requestorUserId, Long userId, String rights, String marker) {
		if (!rightCheck(rights)) {
			return false;
		}

		if (null == marker || marker.isEmpty()) {
			return false;
		}

		if (!retrieveRightGroup(requestorUserId, userId, rights).toUpperCase().contains(marker)) {
			return false;
		}
		return true;
	}

	private static String retrieveRightGroup(Long requestorUserId, Long userId, String rights) {

		String[] groups = rights.split("[+]");
		String ownerRights = groups[0];
		String otherRights = groups[1];

		if (null != userId && requestorUserId.equals(userId)) {
			return ownerRights;
		}
		return otherRights;

	}

}
