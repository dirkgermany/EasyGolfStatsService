package com.egs.app.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.egs.app.ClubStore;
import com.egs.app.HitStore;
import com.egs.app.model.entity.ClubEntity;
import com.egs.app.model.entity.HitsEntity;
import com.egs.app.rest.message.HitsResponse;
import com.egs.app.rest.message.HitsWriteRequest;
import com.egs.app.rest.message.ListClubsResponse;
import com.egs.app.rest.message.ClubListWriteResponse;
import com.egs.app.rest.message.ClubResponse;
import com.egs.app.rest.message.ClubWriteRequest;
import com.egs.app.rest.message.ClubWriteResponse;
import com.egs.app.rest.message.DropResponse;
import com.egs.app.rest.message.ListHitsResponse;
import com.egs.app.rest.message.RestResponse;
import com.egs.exception.CsServiceException;

@RestController
public class ClubController extends MasterController {
	@Autowired
	private ClubStore clubStore;
	
	@GetMapping("/listClubs")
	public ResponseEntity<RestResponse> listClubs(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws CsServiceException {

		try {
			List<ClubEntity> clubEntities = clubStore.listClubsSafe(params, headers);
			if (null != clubEntities) {
				return new ResponseEntity<RestResponse>(new ListClubsResponse(clubEntities), HttpStatus.OK);
			}
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Club list could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(
				new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Club list could not be read", "No hits found"),
				HttpStatus.OK);
	}



	@GetMapping("/getClub")
	public ResponseEntity<RestResponse> getClub(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) throws CsServiceException {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			ClubEntity clubEntity = clubStore.getClubSafe(decodedParams, headers);
			return new ResponseEntity<RestResponse>(new ClubResponse(clubEntity), HttpStatus.OK);
			
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Hits could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
	}

	@PostMapping("/createClub")
	public ResponseEntity<RestResponse> createClub(@RequestBody ClubWriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			ClubEntity clubEntity = clubStore.createClubSafe(requestBody, decodedParams, headers);
			if (null != clubEntity) {
				return new ResponseEntity<RestResponse>(new ClubWriteResponse(clubEntity), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Club could not be created", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Club not created",
				"Club entry still exists, data invalid or not complete"), HttpStatus.OK);
	}


	@PostMapping("/createClubs")
	public ResponseEntity<RestResponse> createClubs(@RequestBody List<ClubWriteRequest> requestBodyList,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		ClubListWriteResponse response = new ClubListWriteResponse(null);
		response.setHttpStatus(HttpStatus.OK);
		
		List<ClubEntity> clubs = new ArrayList<>();
		Iterator<ClubWriteRequest> it = requestBodyList.iterator();
		while (it.hasNext()) {
			ClubWriteRequest requestBody = it.next();
			
			try {
				ClubEntity clubEntity = clubStore.createClubSafe(requestBody, decodedParams, headers);
				if (null != clubEntity) {
					clubs.add(clubEntity);
				}
			} catch (CsServiceException e) {
				response.setHttpStatus(HttpStatus.CONFLICT);
				response.setResult("One or more clubs not stored. Stored clubs see attached list.");
				response.setDescription(response.getDescription() + "\nClub " + requestBody.getClubEntity().getClubName() + " already exists or could not be stored;");
				response.setDescription("One or more clubs are not stored");
			}
			
		}
		if (clubs.size() > 0) {
			response.setClubs(clubs);
			return new ResponseEntity<RestResponse>(response, HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Clubs are not created",
				"Entries still exist, data invalid or not complete"), HttpStatus.OK);
	}


	@PutMapping("/updateClub")
	public ResponseEntity<RestResponse> updateClub(@RequestBody ClubWriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			ClubEntity clubEntity = clubStore.updateClubSafe(requestBody, decodedParams, headers);
			if (null != clubEntity) {
				return new ResponseEntity<RestResponse>(new ClubResponse(clubEntity), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Club could not be updated", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Club not updated",
				"Club entry does not exist, data invalid or not complete"), HttpStatus.OK);
	}

	@DeleteMapping("/deleteClub")
	public ResponseEntity<RestResponse> deleteClub(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}

		String clubIdAsString = decodedParams.get("clubId");
		if (null == clubIdAsString) {
			clubIdAsString = headers.get("clubid");
		}
		if (clubIdAsString == null || clubIdAsString.isEmpty()) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.BAD_REQUEST, "Club not deleted", "clubId is empty"),
					HttpStatus.OK);
		}

		try {
			clubStore.dropClubSafe(decodedParams, headers);
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Club not deleted", dse.getMessage()),
					HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new DropResponse(HttpStatus.OK), HttpStatus.OK);
	}
}