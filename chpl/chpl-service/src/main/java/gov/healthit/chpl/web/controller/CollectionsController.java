package gov.healthit.chpl.web.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.healthit.chpl.domain.DeveloperTransparency;
import gov.healthit.chpl.domain.search.BasicSearchResponse;
import gov.healthit.chpl.domain.search.CertifiedProductFlatSearchResult;
import gov.healthit.chpl.domain.search.CertifiedProductSearchResult;
import gov.healthit.chpl.domain.search.SearchViews;
import gov.healthit.chpl.manager.CertifiedProductSearchManager;
import gov.healthit.chpl.manager.DeveloperManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "collections")
@RestController
@RequestMapping("/collections")
public class CollectionsController {
	private static final Logger logger = LogManager.getLogger(CollectionsController.class);
	@Autowired private CertifiedProductSearchManager certifiedProductSearchManager;
	@Autowired private DeveloperManager developerManager;
		
	@ApiOperation(value="Get basic data about all certified products in the system.", 
			notes="")
	@RequestMapping(value="/certified_products", method = RequestMethod.GET,
			produces="application/json; charset = utf-8")
	public @ResponseBody String getAllCertifiedProducts(@RequestParam(value="fields", required = false) String delimitedFieldNames) 
	throws JsonProcessingException {
		
		List<CertifiedProductFlatSearchResult> cachedSearchResults = certifiedProductSearchManager.search();
		
		String result = "";		
		if(!StringUtils.isEmpty(delimitedFieldNames)) {
			//write out objects as json but do not include properties with a null value
			ObjectMapper nonNullJsonMapper = new ObjectMapper();
			nonNullJsonMapper.setSerializationInclusion(Include.NON_NULL);
			nonNullJsonMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
			
			//create a copy of the search results since we will be manipulating them 
			//by setting fields to null but do not want to overwrite the cached data
			List<CertifiedProductFlatSearchResult> mutableSearchResults = new ArrayList<CertifiedProductFlatSearchResult>(cachedSearchResults.size());
			for(CertifiedProductFlatSearchResult cachedSearchResult : cachedSearchResults) {
				mutableSearchResults.add(new CertifiedProductFlatSearchResult(cachedSearchResult));
			}
			
			//parse the field names that we want to send back
			String[] fieldNames = delimitedFieldNames.split(",");
			List<String> requiredFields = new ArrayList<String>(fieldNames.length);
			for(int i = 0; i < fieldNames.length; i++) {
				requiredFields.add(fieldNames[i].toUpperCase());
			}
			
			//compare all the field names in the results with the required field names
			List<Field> searchResultFields = getAllInheritedFields(CertifiedProductFlatSearchResult.class, new ArrayList<Field>());
			for(Field searchResultField : searchResultFields) {
				//is this searchResultField a required one?
				boolean isSearchResultFieldRequired = false;
				for(String requiredField : requiredFields) {
					if(searchResultField.getName().equalsIgnoreCase(requiredField)) {
						isSearchResultFieldRequired = true;
					}
				}
				
				//if the field is not required, set it to null
				//assumes standard java bean getter/setter names
				if(!isSearchResultFieldRequired && !searchResultField.getName().equalsIgnoreCase("serialVersionUID")) {
					//what type is the field? String? Long?
					Class searchResultFieldTypeClazz = searchResultField.getType();
					//find the setter method that accepts the correct type
					String firstUppercaseChar = searchResultField.getName().charAt(0)+"";
					firstUppercaseChar = firstUppercaseChar.toUpperCase();
					String setterMethodName = "set" + firstUppercaseChar + searchResultField.getName().substring(1);
					try {
						Method setter = CertifiedProductFlatSearchResult.class.getMethod(setterMethodName, searchResultFieldTypeClazz);
						//call the setter method and set to null
						if(setter != null) {
							for(CertifiedProductSearchResult searchResult : mutableSearchResults) {
								setter.invoke(searchResult, new Object[]{ null });
							}
						} else {
							logger.error("No method with name " + setterMethodName + " was found for field " + searchResultField.getName() + " and argument type " + searchResultFieldTypeClazz.getName());
						}
					} catch(NoSuchMethodException ex) {
						logger.error("No method with name " + setterMethodName + " was found for field " + searchResultField.getName() + " and argument type " + searchResultFieldTypeClazz.getName(), ex);
					} catch(InvocationTargetException ex) {
						logger.error("exception invoking method " + setterMethodName, ex);
					} catch(IllegalArgumentException ex) {
						logger.error("bad arguments to method " + setterMethodName, ex);
					} catch(IllegalAccessException ex) {
						logger.error("Cannot access method " + setterMethodName, ex);
					}
				}
			}
			BasicSearchResponse response = new BasicSearchResponse();
			response.setResults(mutableSearchResults);
			result = nonNullJsonMapper.writeValueAsString(response);
		} else {
			ObjectMapper viewMapper = new ObjectMapper();
			viewMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
			BasicSearchResponse response = new BasicSearchResponse();
			response.setResults(cachedSearchResults);
			 result = viewMapper.writerWithView(SearchViews.Default.class).writeValueAsString(response);
		}

		return result;
	}
	
	@ApiOperation(value="Get a list of all developers with transparency attestation URLs"
			+ "and ACB attestations.", 
			notes="")
	@RequestMapping(value="/developers", method = RequestMethod.GET,
			produces="application/json; charset = utf-8")
	public @ResponseBody List<DeveloperTransparency> getDeveloperCollection() {
		List<DeveloperTransparency> developerResults = developerManager.getDeveloperCollection();
		return developerResults;
	}
	
	private List<Field> getAllInheritedFields(Class clazz, List<Field> fields) {
	    Class superClazz = clazz.getSuperclass();
	    if(superClazz != null){
	    	getAllInheritedFields(superClazz, fields);
	    }
	    fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
	    return fields;
	}
}
