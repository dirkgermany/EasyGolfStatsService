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

import com.egs.app.HitsStore;
import com.egs.app.model.entity.HitsEntity;
import com.egs.app.rest.message.HitsResponse;
import com.egs.app.rest.message.HitsWriteRequest;
import com.egs.app.rest.message.DropResponse;
import com.egs.app.rest.message.HitsListWriteResponse;
import com.egs.app.rest.message.ListHitsResponse;
import com.egs.app.rest.message.RestResponse;
import com.egs.exception.CsServiceException;

@RestController
public class HitsController extends MasterController {
	@Autowired
	private HitsStore hitsStore;
	
	@GetMapping("/listSessionHits")
	public ResponseEntity<RestResponse> listSessionHits(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws CsServiceException {

		try {
			List<HitsEntity> hitsEntities = hitsStore.listSessionHitsSafe(params, headers);
			if (null != hitsEntities) {
				return new ResponseEntity<RestResponse>(new ListHitsResponse(hitsEntities), HttpStatus.OK);
			}
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Hits list could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(
				new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Hits list could not be read", "No hits found"),
				HttpStatus.OK);
	}



	@GetMapping("/getHits")
	public ResponseEntity<RestResponse> getHits(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) throws CsServiceException {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			HitsEntity hitsEntities = hitsStore.getHitsSafe(decodedParams, headers);
			return new ResponseEntity<RestResponse>(new HitsResponse(hitsEntities), HttpStatus.OK);
			
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Hits could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
	}

	@PostMapping("/createHits")
	public ResponseEntity<RestResponse> createHits(@RequestBody HitsWriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			HitsEntity hitsEntity = hitsStore.createHitsSafe(requestBody, decodedParams, headers);
			if (null != hitsEntity) {
				return new ResponseEntity<RestResponse>(new HitsResponse(hitsEntity), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Hits could not be created", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Hits not created",
				"Hits entry still exists, data invalid or not complete"), HttpStatus.OK);
	}
	
	@PostMapping("/createHitsCollection")
	public ResponseEntity<RestResponse> createHitsCollection(@RequestBody List<HitsWriteRequest> requestBodyList,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		HitsListWriteResponse response = new HitsListWriteResponse(null);
		response.setHttpStatus(HttpStatus.OK);
		
		List<HitsEntity> hitsList = new ArrayList<>();
		Iterator<HitsWriteRequest> it = requestBodyList.iterator();
		while (it.hasNext()) {
			HitsWriteRequest requestBody = it.next();
			
			try {
				HitsEntity hitsEntity = hitsStore.createHitsSafe(requestBody, decodedParams, headers);
				if (null != hitsEntity) {
					hitsList.add(hitsEntity);
				}
			} catch (CsServiceException e) {
				response.setHttpStatus(HttpStatus.CONFLICT);
				response.setResult("One or more hit entries not stored. Stored hit entries see attached list.");
				response.setDescription(response.getDescription() + "\nHits " + requestBody.getHitsEntity().getSessionDate() + requestBody.getHitsEntity().getHitCategory().name() + requestBody.getHitsEntity().getClubType() + " already exists or could not be stored;");
				response.setDescription("One or more hit entries are not stored");
			}
		}
		
		if (hitsList.size() > 0) {
			response.setHitsList(hitsList);
			return new ResponseEntity<RestResponse>(response, HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Clubs are not created",
				"Entries still exist, data invalid or not complete"), HttpStatus.OK);
	}

	@PutMapping("/updateHits")
	public ResponseEntity<RestResponse> updateHits(@RequestBody HitsWriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			HitsEntity hitsEntity = hitsStore.updateHitsSafe(requestBody, decodedParams, headers);
			if (null != hitsEntity) {
				return new ResponseEntity<RestResponse>(new HitsResponse(hitsEntity), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Configuration could not be updated", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Hits not updated",
				"Hits entry does not exist, data invalid or not complete"), HttpStatus.OK);
	}

	@DeleteMapping("/deleteHits")
	public ResponseEntity<RestResponse> deleteHits(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}

		String configurationIdAsString = decodedParams.get("configurationId");
		if (null == configurationIdAsString) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Hits not deleted", "Hits id is empty"),
					HttpStatus.OK);
		}

		try {
			hitsStore.dropConfigurationSafe(decodedParams, headers);
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Hits not deleted", dse.getMessage()),
					HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new DropResponse(HttpStatus.NO_CONTENT), HttpStatus.NO_CONTENT);
	}
}