package gov.healthit.chpl.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.dao.ApiKeyActivityDAO;
import gov.healthit.chpl.dao.ApiKeyDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.ApiKeyActivity;
import gov.healthit.chpl.domain.concept.ActivityConcept;
import gov.healthit.chpl.dto.ApiKeyActivityDTO;
import gov.healthit.chpl.dto.ApiKeyDTO;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.ApiKeyManager;

@Service
public class ApiKeyManagerImpl implements ApiKeyManager {

	@Autowired
	private ApiKeyDAO apiKeyDAO;

	@Autowired
	private ApiKeyActivityDAO apiKeyActivityDAO;

	@Autowired
	private ActivityManager activityManager;

	@Override
	@Transactional
	public ApiKeyDTO createKey(ApiKeyDTO toCreate) throws EntityCreationException, JsonProcessingException, EntityRetrievalException {

		ApiKeyDTO created = apiKeyDAO.create(toCreate);

		String activityMsg = "API Key "+created.getApiKey()+" was created.";
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_API_KEY, created.getId(), activityMsg, null, created);
		return created;

	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteKey(Long keyId) throws EntityRetrievalException, JsonProcessingException, EntityCreationException {

		ApiKeyDTO toDelete = apiKeyDAO.getById(keyId);

		String activityMsg = "API Key "+toDelete.getApiKey()+" was revoked.";

		apiKeyDAO.delete(keyId);
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_API_KEY, toDelete.getId(), activityMsg, toDelete, null);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteKey(String keyString) throws JsonProcessingException, EntityCreationException, EntityRetrievalException {

		ApiKeyDTO toDelete = apiKeyDAO.getByKey(keyString);

		String activityMsg = "API Key "+toDelete.getApiKey()+" was revoked.";

		apiKeyDAO.delete(toDelete.getId());
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_API_KEY, toDelete.getId(), activityMsg, toDelete, null);
	}

	@Override
	@Transactional
	public ApiKeyDTO findKey(Long keyId) throws EntityRetrievalException {
		return apiKeyDAO.getById(keyId);
	}

	@Override
	@Transactional
	public ApiKeyDTO findKey(String keyString) throws EntityRetrievalException {
		return apiKeyDAO.getByKey(keyString);
	}

	@Override
	@Transactional
	public void logApiKeyActivity(String keyString, String apiCallPath)
	throws EntityRetrievalException, EntityCreationException {

		ApiKeyDTO apiKey = findKey(keyString);
		ApiKeyActivityDTO apiKeyActivityDto = new ApiKeyActivityDTO();

		apiKeyActivityDto.setApiCallPath(apiCallPath);
		apiKeyActivityDto.setApiKeyId(apiKey.getId());
		apiKeyActivityDto.setDeleted(false);

		apiKeyActivityDAO.create(apiKeyActivityDto);
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
	public List<ApiKeyActivity> getApiKeyActivity(String keyString) throws EntityRetrievalException {

		ApiKeyDTO apiKey = findKey(keyString);
		if (apiKey == null){
			apiKey = apiKeyDAO.getRevokedKeyByKey(keyString);
		}

		List<ApiKeyActivityDTO> activityDTOs = apiKeyActivityDAO.findByKeyId(apiKey.getId());
		List<ApiKeyActivity> activity = new ArrayList<ApiKeyActivity>();

		for (ApiKeyActivityDTO dto : activityDTOs){

			ApiKeyActivity apiKeyActivity = new ApiKeyActivity();

			apiKeyActivity.setApiKey(apiKey.getApiKey());
			apiKeyActivity.setApiKeyId(apiKey.getId());
			apiKeyActivity.setEmail(apiKey.getEmail());
			apiKeyActivity.setName(apiKey.getNameOrganization());
			apiKeyActivity.setId(dto.getId());
			apiKeyActivity.setCreationDate(dto.getCreationDate());
			apiKeyActivity.setApiCallPath(dto.getApiCallPath());

			activity.add(apiKeyActivity);

		}
		return activity;
	}

	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
	public List<ApiKeyActivity> getApiKeyActivity(String keyString, Integer pageNumber, Integer pageSize)
	throws EntityRetrievalException {

		ApiKeyDTO apiKey = findKey(keyString);
		if (apiKey == null){
			apiKey = apiKeyDAO.getRevokedKeyByKey(keyString);
		}

		List<ApiKeyActivityDTO> activityDTOs = apiKeyActivityDAO.findByKeyId(apiKey.getId(), pageNumber, pageSize);
		List<ApiKeyActivity> activity = new ArrayList<ApiKeyActivity>();

		for (ApiKeyActivityDTO dto : activityDTOs){

			ApiKeyActivity apiKeyActivity = new ApiKeyActivity();

			apiKeyActivity.setApiKey(apiKey.getApiKey());
			apiKeyActivity.setApiKeyId(apiKey.getId());
			apiKeyActivity.setEmail(apiKey.getEmail());
			apiKeyActivity.setName(apiKey.getNameOrganization());
			apiKeyActivity.setId(dto.getId());
			apiKeyActivity.setCreationDate(dto.getCreationDate());
			apiKeyActivity.setApiCallPath(dto.getApiCallPath());

			activity.add(apiKeyActivity);

		}
		return activity;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<ApiKeyDTO> findAll() {
		return apiKeyDAO.findAll();
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
	public List<ApiKeyActivity> getApiKeyActivity() throws EntityRetrievalException {

		List<ApiKeyActivityDTO> activityDTOs = apiKeyActivityDAO.findAll();
		List<ApiKeyActivity> activity = new ArrayList<ApiKeyActivity>();

		for (ApiKeyActivityDTO dto : activityDTOs){

			ApiKeyDTO apiKey = findKey(dto.getApiKeyId());
			if (apiKey == null){
				apiKey = apiKeyDAO.getRevokedKeyById(dto.getApiKeyId());
			}

			ApiKeyActivity apiKeyActivity = new ApiKeyActivity();

			apiKeyActivity.setApiKey(apiKey.getApiKey());
			apiKeyActivity.setApiKeyId(apiKey.getId());
			apiKeyActivity.setEmail(apiKey.getEmail());
			apiKeyActivity.setName(apiKey.getNameOrganization());
			apiKeyActivity.setId(dto.getId());
			apiKeyActivity.setCreationDate(dto.getCreationDate());
			apiKeyActivity.setApiCallPath(dto.getApiCallPath());

			activity.add(apiKeyActivity);
		}

		return activity;
	}

	/* Gets API key activity within the constraints of the provided parameters
	 * Parameters:
	 * String apiKeyFilter - string of API key(s)
	 * Integer pageNumber - The page for the API key activity
	 * Integer pageSize - Number of API keys on the page
	 * boolean dateAscending - True if dateAscending; false if dateDescending
	 * Long startDate - Start date, in milliseconds, for API key creation
	 * Long endDate - End date, in milliseconds, for API key creation
	 * Returns: list of ApiKeyActivity
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ONC_STAFF')")
	public List<ApiKeyActivity> getApiKeyActivity
	(String apiKeyFilter, Integer pageNumber, Integer pageSize, boolean dateAscending, Long startDateMilli, Long endDateMilli)
			throws EntityRetrievalException {
		List<ApiKeyActivityDTO> activityDTOs = apiKeyActivityDAO.getApiKeyActivity
				(apiKeyFilter, pageNumber, pageSize, dateAscending, startDateMilli, endDateMilli);
		List<ApiKeyActivity> apiKeyActivitiesList = new ArrayList<ApiKeyActivity>();

		for (ApiKeyActivityDTO dto : activityDTOs){
			ApiKeyDTO apiKey = findKey(dto.getApiKeyId());
			if (apiKey == null){
				apiKey = apiKeyDAO.getRevokedKeyById(dto.getApiKeyId());
			}

			ApiKeyActivity apiKeyActivity = new ApiKeyActivity();

			apiKeyActivity.setApiKey(apiKey.getApiKey());
			apiKeyActivity.setApiKeyId(apiKey.getId());
			apiKeyActivity.setEmail(apiKey.getEmail());
			apiKeyActivity.setName(apiKey.getNameOrganization());

			apiKeyActivity.setId(dto.getId());
			apiKeyActivity.setCreationDate(dto.getCreationDate());
			apiKeyActivity.setApiCallPath(dto.getApiCallPath());

			apiKeyActivitiesList.add(apiKeyActivity);
		}
		return apiKeyActivitiesList;
	}
}