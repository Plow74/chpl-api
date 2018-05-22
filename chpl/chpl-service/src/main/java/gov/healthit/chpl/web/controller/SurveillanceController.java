package gov.healthit.chpl.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.auth.dto.UserDTO;
import gov.healthit.chpl.auth.manager.UserManager;
import gov.healthit.chpl.auth.permission.UserPermissionRetrievalException;
import gov.healthit.chpl.auth.user.UserRetrievalException;
import gov.healthit.chpl.caching.CacheNames;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.domain.IdListContainer;
import gov.healthit.chpl.domain.Job;
import gov.healthit.chpl.domain.SimpleExplainableAction;
import gov.healthit.chpl.domain.Surveillance;
import gov.healthit.chpl.domain.SurveillanceNonconformityDocument;
import gov.healthit.chpl.domain.concept.ActivityConcept;
import gov.healthit.chpl.domain.concept.JobTypeConcept;
import gov.healthit.chpl.dto.CertificationBodyDTO;
import gov.healthit.chpl.dto.CertifiedProductDTO;
import gov.healthit.chpl.dto.job.JobDTO;
import gov.healthit.chpl.dto.job.JobTypeDTO;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.CertificationBodyManager;
import gov.healthit.chpl.manager.CertifiedProductDetailsManager;
import gov.healthit.chpl.manager.CertifiedProductManager;
import gov.healthit.chpl.manager.JobManager;
import gov.healthit.chpl.manager.SurveillanceManager;
import gov.healthit.chpl.manager.SurveillanceUploadManager;
import gov.healthit.chpl.manager.impl.SurveillanceAuthorityAccessDeniedException;
import gov.healthit.chpl.util.FileUtils;
import gov.healthit.chpl.validation.surveillance.SurveillanceValidator;
import gov.healthit.chpl.web.controller.exception.MissingReasonException;
import gov.healthit.chpl.web.controller.exception.ObjectMissingValidationException;
import gov.healthit.chpl.web.controller.exception.ObjectsMissingValidationException;
import gov.healthit.chpl.web.controller.exception.ValidationException;
import gov.healthit.chpl.web.controller.results.SurveillanceResults;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "surveillance")
@RestController
@RequestMapping("/surveillance")
public class SurveillanceController implements MessageSourceAware {

    private static final Logger LOGGER = LogManager.getLogger(SurveillanceController.class);
    private final JobTypeConcept allowedJobType = JobTypeConcept.SURV_UPLOAD;
    
    @Autowired
    Environment env;
    @Autowired
    MessageSource messageSource;
    @Autowired
    private UserManager userManager;
    @Autowired
    private JobManager jobManager;
    @Autowired
    private SurveillanceManager survManager;
    @Autowired
    private SurveillanceUploadManager survUploadManager;
    @Autowired
    private CertifiedProductManager cpManager;
    @Autowired
    private ActivityManager activityManager;
    @Autowired
    private CertifiedProductDetailsManager cpdetailsManager;
    @Autowired
    private CertificationBodyManager acbManager;
    @Autowired
    private SurveillanceValidator survValidator;

    @ApiOperation(value = "Get the listing of all pending surveillance items that this user has access to.")
    @RequestMapping(value = "/pending", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody SurveillanceResults getAllPendingSurveillanceForAcbUser() {
        List<CertificationBodyDTO> acbs = acbManager.getAllForUser(false);
        List<Surveillance> pendingSurvs = new ArrayList<Surveillance>();

        if (acbs != null) {
            for (CertificationBodyDTO acb : acbs) {
                try {
                    List<Surveillance> survsOnAcb = survManager.getPendingByAcb(acb.getId());
                    pendingSurvs.addAll(survsOnAcb);
                } catch (final AccessDeniedException denied) {
                    LOGGER.warn("Access denied to pending surveillance for acb " + acb.getName() + " and user "
                            + Util.getUsername());
                }
            }
        }

        SurveillanceResults results = new SurveillanceResults();
        results.setPendingSurveillance(pendingSurvs);
        return results;
    }

    @ApiOperation(value = "Download nonconformity supporting documentation.",
            notes = "Download a specific file that was previously uploaded to a surveillance nonconformity.")
    @RequestMapping(value = "/document/{documentId}", method = RequestMethod.GET)
    public void streamDocumentContents(@PathVariable("documentId") Long documentId, HttpServletResponse response)
            throws EntityRetrievalException, IOException {
        SurveillanceNonconformityDocument doc = survManager.getDocumentById(documentId, true);

        if (doc != null && doc.getFileContents() != null && doc.getFileContents().length > 0) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(doc.getFileContents());
            // get MIME type of the file
            String mimeType = doc.getFileType();
            if (mimeType == null) {
                // set to binary type if MIME mapping not found
                mimeType = "application/octet-stream";
            }
            // set content attributes for the response
            response.setContentType(mimeType);
            response.setContentLength(doc.getFileContents().length);

            // set headers for the response
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", doc.getFileName());
            response.setHeader(headerKey, headerValue);

            // get output stream of the response
            OutputStream outStream = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead = -1;

            // write bytes read from the input stream into the output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outStream.close();
        }
    }

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Create a new surveillance activity for a certified product.",
            notes = "Creates a new surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system and associates them with the certified product indicated in the "
                    + "request body. The surveillance passed into this request will first be validated "
                    + " to check for errors. " + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public synchronized ResponseEntity<Surveillance> createSurveillanceDeprecated(
            @RequestBody(required = true) Surveillance survToInsert) throws ValidationException,
            EntityRetrievalException, CertificationBodyAccessException, UserPermissionRetrievalException,
            EntityCreationException, JsonProcessingException, SurveillanceAuthorityAccessDeniedException {

        return create(survToInsert);
    }

    @ApiOperation(value = "Create a new surveillance activity for a certified product.",
            notes = "Creates a new surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system and associates them with the certified product indicated in the "
                    + "request body. The surveillance passed into this request will first be validated "
                    + " to check for errors. " + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public synchronized ResponseEntity<Surveillance> createSurveillance(
            @RequestBody(required = true) Surveillance survToInsert) throws ValidationException,
            EntityRetrievalException, CertificationBodyAccessException, UserPermissionRetrievalException,
            EntityCreationException, JsonProcessingException, SurveillanceAuthorityAccessDeniedException {

        return create(survToInsert);
    }

    private synchronized ResponseEntity<Surveillance> create(Surveillance survToInsert) throws ValidationException,
            EntityRetrievalException, CertificationBodyAccessException, UserPermissionRetrievalException,
            EntityCreationException, JsonProcessingException, SurveillanceAuthorityAccessDeniedException {
        survToInsert.getErrorMessages().clear();

        // validate first. this ensures we have all the info filled in
        // that we need to continue
        survManager.validate(survToInsert);

        if (survToInsert.getErrorMessages() != null && survToInsert.getErrorMessages().size() > 0) {
            throw new ValidationException(survToInsert.getErrorMessages(), null);
        }

        // look up the ACB
        CertifiedProductSearchDetails beforeCp = cpdetailsManager
                .getCertifiedProductDetails(survToInsert.getCertifiedProduct().getId());
        CertificationBodyDTO owningAcb = null;
        try {
            owningAcb = acbManager.getById(new Long(beforeCp.getCertifyingBody().get("id").toString()));
        } catch (final AccessDeniedException ex) {
            throw new CertificationBodyAccessException(
                    "User does not have permission to add surveillance to a certified product under ACB "
                            + beforeCp.getCertifyingBody().get("name"));
        } catch (final EntityRetrievalException ex) {
            LOGGER.error("Error looking up ACB associated with surveillance.", ex);
            throw new EntityRetrievalException("Error looking up ACB associated with surveillance.");
        }

        // insert the surveillance
        HttpHeaders responseHeaders = new HttpHeaders();
        Long insertedSurv = null;
        try {
            insertedSurv = survManager.createSurveillance(owningAcb.getId(), survToInsert);
            responseHeaders.set("Cache-cleared", CacheNames.COLLECTIONS_LISTINGS);
        } catch (final SurveillanceAuthorityAccessDeniedException ex) {
            LOGGER.error("User lacks authority to delete surveillance");
            throw new SurveillanceAuthorityAccessDeniedException("User lacks authority to delete surveillance");
        }

        if (insertedSurv == null) {
            throw new EntityCreationException("Error creating new surveillance.");
        }

        CertifiedProductSearchDetails afterCp = cpdetailsManager
                .getCertifiedProductDetails(survToInsert.getCertifiedProduct().getId());
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, afterCp.getId(),
                "Surveillance was added to certified product " + afterCp.getChplProductNumber(), beforeCp, afterCp);

        // query the inserted surveillance
        Surveillance result = survManager.getById(insertedSurv);
        return new ResponseEntity<Surveillance>(result, responseHeaders, HttpStatus.OK);
    }

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Add documentation to an existing nonconformity.",
            notes = "Upload a file of any kind (current size limit 5MB) as supporting "
                    + " documentation to an existing nonconformity. The logged in user uploading the file "
                    + " must have either ROLE_ADMIN or ROLE_ACB and administrative "
                    + " authority on the associated ACB.")
    @RequestMapping(value = "/{surveillanceId}/nonconformity/{nonconformityId}/document/create",
            method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody String uploadNonconformityDocumentDeprecated(@PathVariable("surveillanceId") Long surveillanceId,
            @PathVariable("nonconformityId") Long nonconformityId, @RequestParam("file") MultipartFile file)
            throws InvalidArgumentsException, MaxUploadSizeExceededException, EntityRetrievalException,
            EntityCreationException, IOException {

        return createNonconformityDocumentForSurveillance(surveillanceId, nonconformityId, file);
    }

    @ApiOperation(value = "Add documentation to an existing nonconformity.",
            notes = "Upload a file of any kind (current size limit 5MB) as supporting "
                    + " documentation to an existing nonconformity. The logged in user uploading the file "
                    + " must have either ROLE_ADMIN or ROLE_ACB and administrative "
                    + " authority on the associated ACB.")
    @RequestMapping(value = "/{surveillanceId}/nonconformity/{nonconformityId}/document",
            method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody String uploadNonconformityDocument(@PathVariable("surveillanceId") Long surveillanceId,
            @PathVariable("nonconformityId") Long nonconformityId, @RequestParam("file") MultipartFile file)
            throws InvalidArgumentsException, MaxUploadSizeExceededException, EntityRetrievalException,
            EntityCreationException, IOException {

        return createNonconformityDocumentForSurveillance(surveillanceId, nonconformityId, file);
    }
    
    private String createNonconformityDocumentForSurveillance(Long surveillanceId, Long nonconformityId, MultipartFile file)
            throws InvalidArgumentsException, MaxUploadSizeExceededException, EntityRetrievalException,
            EntityCreationException, IOException {
        if (file.isEmpty()) {
            throw new InvalidArgumentsException("You cannot upload an empty file!");
        }

        Surveillance surv = survManager.getById(surveillanceId);
        CertifiedProductSearchDetails beforeCp = cpdetailsManager
                .getCertifiedProductDetails(surv.getCertifiedProduct().getId());

        SurveillanceNonconformityDocument toInsert = new SurveillanceNonconformityDocument();
        toInsert.setFileContents(file.getBytes());
        toInsert.setFileName(file.getOriginalFilename());
        toInsert.setFileType(file.getContentType());

        CertificationBodyDTO owningAcb = null;
        try {
            owningAcb = acbManager.getById(new Long(beforeCp.getCertifyingBody().get("id").toString()));
        } catch (Exception ex) {
            LOGGER.error("Error looking up ACB associated with surveillance.", ex);
            throw new EntityRetrievalException("Error looking up ACB associated with surveillance.");
        }

        Long insertedDocId = survManager.addDocumentToNonconformity(owningAcb.getId(), nonconformityId, toInsert);
        if (insertedDocId == null) {
            throw new EntityCreationException("Error adding a document to nonconformity with id " + nonconformityId);
        }

        CertifiedProductSearchDetails afterCp = cpdetailsManager
                .getCertifiedProductDetails(surv.getCertifiedProduct().getId());
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT,
                beforeCp.getId(), "Documentation " + toInsert.getFileName()
                        + " was added to a nonconformity for certified product " + afterCp.getChplProductNumber(),
                beforeCp, afterCp);
        return "{\"success\": \"true\"}";
    }

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Update a surveillance activity for a certified product.",
            notes = "Updates an existing surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system. The surveillance passed into this request will first be validated "
                    + " to check for errors. " + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public synchronized ResponseEntity<Surveillance> updateSurveillanceDeprecated(
            @RequestBody(required = true) Surveillance survToUpdate)
            throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException,
            JsonProcessingException, SurveillanceAuthorityAccessDeniedException {

        return update(survToUpdate);
    }

    @ApiOperation(value = "Update a surveillance activity for a certified product.",
            notes = "Updates an existing surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system. The surveillance passed into this request will first be validated "
                    + " to check for errors. " + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "/{surveillanceId}", method = RequestMethod.PUT, produces = "application/json; charset=utf-8")
    public synchronized ResponseEntity<Surveillance> updateSurveillance(
            @RequestBody(required = true) Surveillance survToUpdate)
            throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException,
            JsonProcessingException, SurveillanceAuthorityAccessDeniedException {

        return update(survToUpdate);
    }

    private synchronized ResponseEntity<Surveillance> update(Surveillance survToUpdate)
            throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException,
            JsonProcessingException, SurveillanceAuthorityAccessDeniedException {
        survToUpdate.getErrorMessages().clear();

        // validate first. this ensures we have all the info filled in
        // that we need to continue
        survManager.validate(survToUpdate);

        if (survToUpdate.getErrorMessages() != null && survToUpdate.getErrorMessages().size() > 0) {
            throw new ValidationException(survToUpdate.getErrorMessages(), null);
        }

        // look up the ACB
        CertifiedProductSearchDetails beforeCp = cpdetailsManager
                .getCertifiedProductDetails(survToUpdate.getCertifiedProduct().getId());
        CertificationBodyDTO owningAcb = null;
        try {
            owningAcb = acbManager.getById(new Long(beforeCp.getCertifyingBody().get("id").toString()));
        } catch (Exception ex) {
            LOGGER.error("Error looking up ACB associated with surveillance.", ex);
            throw new EntityRetrievalException("Error looking up ACB associated with surveillance.");
        }

        // update the surveillance
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            survManager.updateSurveillance(owningAcb.getId(), survToUpdate);
            responseHeaders.set("Cache-cleared", CacheNames.COLLECTIONS_LISTINGS);
        } catch (final SurveillanceAuthorityAccessDeniedException ex) {
            LOGGER.error("User lacks authority to update surveillance");
            throw new SurveillanceAuthorityAccessDeniedException("User lacks authority to update surveillance");
        } catch (Exception ex) {
            LOGGER.error("Error updating surveillance with id " + survToUpdate.getId());
        }

        CertifiedProductSearchDetails afterCp = cpdetailsManager
                .getCertifiedProductDetails(survToUpdate.getCertifiedProduct().getId());
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, afterCp.getId(),
                "Surveillance was updated on certified product " + afterCp.getChplProductNumber(), beforeCp, afterCp);

        // query the inserted surveillance
        Surveillance result = survManager.getById(survToUpdate.getId());
        return new ResponseEntity<Surveillance>(result, responseHeaders, HttpStatus.OK);
    }

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Delete a surveillance activity for a certified product.",
            notes = "Deletes an existing surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system. " + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "/{surveillanceId}/delete", method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public synchronized @ResponseBody ResponseEntity<String> deleteSurveillanceDeprecated(
            @PathVariable(value = "surveillanceId") Long surveillanceId,
            @RequestBody(required = false) SimpleExplainableAction requestBody)
            throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException,
            JsonProcessingException, AccessDeniedException, SurveillanceAuthorityAccessDeniedException,
            MissingReasonException {
        
        return delete(surveillanceId, requestBody);
    }
    
    @ApiOperation(value = "Delete a surveillance activity for a certified product.",
            notes = "Deletes an existing surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system. " + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "/{surveillanceId}", method = RequestMethod.DELETE,
            produces = "application/json; charset=utf-8")
    public synchronized @ResponseBody ResponseEntity<String> deleteSurveillance(
            @PathVariable(value = "surveillanceId") Long surveillanceId,
            @RequestBody(required = false) SimpleExplainableAction requestBody)
            throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException,
            JsonProcessingException, AccessDeniedException, SurveillanceAuthorityAccessDeniedException,
            MissingReasonException {
        
        return delete(surveillanceId, requestBody);
    }

    private synchronized ResponseEntity<String> delete(Long surveillanceId, SimpleExplainableAction requestBody)
            throws InvalidArgumentsException, ValidationException, EntityCreationException, EntityRetrievalException,
            JsonProcessingException, AccessDeniedException, SurveillanceAuthorityAccessDeniedException,
            MissingReasonException {
        Surveillance survToDelete = survManager.getById(surveillanceId);

        if (survToDelete == null) {
            throw new InvalidArgumentsException("Cannot find surveillance with id " + surveillanceId + " to delete.");
        }

        survValidator.validateSurveillanceAuthority(survToDelete);
        if (survToDelete.getErrorMessages() != null && survToDelete.getErrorMessages().size() > 0) {
            throw new ValidationException(survToDelete.getErrorMessages(), null);
        }

        CertifiedProductSearchDetails beforeCp = cpdetailsManager
                .getCertifiedProductDetails(survToDelete.getCertifiedProduct().getId());
        CertificationBodyDTO owningAcb = 
                acbManager.getById(new Long(beforeCp.getCertifyingBody().get("id").toString()));

        HttpHeaders responseHeaders = new HttpHeaders();
        // delete it
        try {
            survManager.deleteSurveillance(owningAcb.getId(), survToDelete);
            responseHeaders.set("Cache-cleared", CacheNames.COLLECTIONS_LISTINGS);
        } catch (final SurveillanceAuthorityAccessDeniedException ex) {
            LOGGER.error("User lacks authority to delete surveillance");
            throw new SurveillanceAuthorityAccessDeniedException("User lacks authority to delete surveillance");
        } catch (Exception ex) {
            LOGGER.error("Error deleting surveillance with id " + survToDelete.getId() + " during an update.");
        }

        CertifiedProductSearchDetails afterCp = cpdetailsManager
                .getCertifiedProductDetails(survToDelete.getCertifiedProduct().getId());
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, afterCp.getId(),
                "Surveillance was delete from certified product " + afterCp.getChplProductNumber(), 
                beforeCp, afterCp, requestBody.getReason());

        return new ResponseEntity<String>("{\"success\" : true}", responseHeaders, HttpStatus.OK);
    }

    @Deprecated
    @ApiOperation(value = "DEPRECATED.  Remove documentation from a nonconformity.",
            notes = "The logged in user" + " must have either ROLE_ADMIN or ROLE_ACB and administrative "
                    + " authority on the associated ACB.")
    @RequestMapping(value = "/{surveillanceId}/document/{docId}/delete", method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public String deleteNonconformityDocumentDeprecated(@PathVariable("surveillanceId") Long surveillanceId,
            @PathVariable("docId") Long docId) throws JsonProcessingException, EntityCreationException,
            EntityRetrievalException, InvalidArgumentsException {

        return deleteNonconformityDocument(surveillanceId, docId);
    }

    @ApiOperation(value = "Remove documentation from a nonconformity.",
            notes = "The logged in user" + " must have either ROLE_ADMIN or ROLE_ACB and administrative "
                    + " authority on the associated ACB.")
    @RequestMapping(value = "/{surveillanceId}/document/{docId}", method = RequestMethod.DELETE,
            produces = "application/json; charset=utf-8")
    public String deleteNonconformityDocumentFromSurveillance(@PathVariable("surveillanceId") Long surveillanceId,
            @PathVariable("docId") Long docId) throws JsonProcessingException, EntityCreationException,
            EntityRetrievalException, InvalidArgumentsException {

        return deleteNonconformityDocument(surveillanceId, docId);
    }
    
    private String deleteNonconformityDocument(Long surveillanceId, Long docId) 
                throws JsonProcessingException, EntityCreationException, EntityRetrievalException, 
                InvalidArgumentsException {

        Surveillance surv = survManager.getById(surveillanceId);
        if (surv == null) {
            throw new InvalidArgumentsException("Cannot find surveillance with id " + surveillanceId + " to delete.");
        }

        CertifiedProductSearchDetails beforeCp = cpdetailsManager
                .getCertifiedProductDetails(surv.getCertifiedProduct().getId());
        CertificationBodyDTO owningAcb = null;
        try {
            owningAcb = acbManager.getById(new Long(beforeCp.getCertifyingBody().get("id").toString()));
        } catch (Exception ex) {
            LOGGER.error("Error looking up ACB associated with surveillance.", ex);
            throw new EntityRetrievalException("Error looking up ACB associated with surveillance.");
        }

        try {
            survManager.deleteNonconformityDocument(owningAcb.getId(), docId);
        } catch (Exception ex) {
            throw ex;
        }

        CertifiedProductSearchDetails afterCp = cpdetailsManager
                .getCertifiedProductDetails(surv.getCertifiedProduct().getId());
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, beforeCp.getId(),
                "A document was removed from a nonconformity for certified product " + afterCp.getChplProductNumber(),
                beforeCp, afterCp);
        return "{\"success\": \"true\"}";
    }

    @ApiOperation(value = "Reject (effectively delete) a pending surveillance item.")
    @RequestMapping(value = "/pending/{pendingSurvId}/reject", method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String deletePendingSurveillance(@PathVariable("pendingSurvId") Long id)
            throws EntityNotFoundException, AccessDeniedException, ObjectMissingValidationException,
            JsonProcessingException, EntityRetrievalException, EntityCreationException {
        List<CertificationBodyDTO> acbs = acbManager.getAllForUser(false);
        survManager.deletePendingSurveillance(acbs, id, false);
        return "{\"success\" : true}";
    }

    @ApiOperation(value = "Reject several pending surveillance.",
            notes = "Marks a list of pending surveillance as deleted. ROLE_ACB "
                    + " and administrative authority on the ACB for each pending surveillance is required.")
    @RequestMapping(value = "/pending/reject", method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String deletePendingSurveillance(@RequestBody IdListContainer idList)
            throws EntityRetrievalException, JsonProcessingException, EntityCreationException, EntityNotFoundException,
            AccessDeniedException, InvalidArgumentsException, ObjectsMissingValidationException {
        if (idList == null || idList.getIds() == null || idList.getIds().size() == 0) {
            throw new InvalidArgumentsException("At least one id must be provided for rejection.");
        }

        ObjectsMissingValidationException possibleExceptions = new ObjectsMissingValidationException();
        List<CertificationBodyDTO> acbs = acbManager.getAllForUser(false);
        for (Long id : idList.getIds()) {
            try {
                survManager.deletePendingSurveillance(acbs, id, false);
            } catch (final ObjectMissingValidationException ex) {
                possibleExceptions.getExceptions().add(ex);
            }
        }

        if (possibleExceptions.getExceptions() != null && possibleExceptions.getExceptions().size() > 0) {
            throw possibleExceptions;
        }
        return "{\"success\" : true}";
    }

    @ApiOperation(value = "Confirm a pending surveillance activity.",
            notes = "Creates a new surveillance activity, surveilled requirements, and any applicable non-conformities "
                    + "in the system and associates them with the certified product indicated in the "
                    + "request body. If the surveillance is an update of an existing surveillance activity "
                    + "as indicated by the 'surveillanceIdToReplace' field, that existing surveillance "
                    + "activity will be marked as deleted and the surveillance in this request body will "
                    + "be inserted. The surveillance passed into this request will first be validated "
                    + " to check for errors and the related pending surveillance will be removed. "
                    + "ROLE_ACB "
                    + " and administrative authority on the ACB associated with the certified product is required.")
    @RequestMapping(value = "/pending/confirm", method = RequestMethod.POST,
            produces = "application/json; charset=utf-8")
    public synchronized ResponseEntity<Surveillance> confirmPendingSurveillance(
            @RequestBody(required = true) Surveillance survToInsert)
            throws ValidationException, EntityRetrievalException, EntityCreationException, JsonProcessingException,
            UserPermissionRetrievalException, SurveillanceAuthorityAccessDeniedException {
        if (survToInsert == null || survToInsert.getId() == null) {
            throw new ValidationException("An id must be provided in the request body.");
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        CertifiedProductSearchDetails beforeCp = cpdetailsManager
                .getCertifiedProductDetails(survToInsert.getCertifiedProduct().getId());
        CertificationBodyDTO owningAcb = null;
        try {
            owningAcb = acbManager.getById(new Long(beforeCp.getCertifyingBody().get("id").toString()));
        } catch (Exception ex) {
            LOGGER.error("Error looking up ACB associated with surveillance.", ex);
            throw new EntityRetrievalException("Error looking up ACB associated with surveillance.");
        }

        Long pendingSurvToDelete = survToInsert.getId();
        if (survManager.isPendingSurveillanceAvailableForUpdate(owningAcb.getId(), pendingSurvToDelete)) {
            survToInsert.getErrorMessages().clear();

            // validate first. this ensures we have all the info filled in
            // that we need to continue
            survManager.validate(survToInsert);
            if (survToInsert.getErrorMessages() != null && survToInsert.getErrorMessages().size() > 0) {
                throw new ValidationException(survToInsert.getErrorMessages(), null);
            }

            // insert or update the surveillance
            Long insertedSurv = survManager.createSurveillance(owningAcb.getId(), survToInsert);
            responseHeaders.set("Cache-cleared", CacheNames.COLLECTIONS_LISTINGS);
            if (insertedSurv == null) {
                throw new EntityCreationException("Error creating new surveillance.");
            }

            // delete the pending surveillance item if this one was successfully
            // inserted
            try {
                survManager.deletePendingSurveillance(owningAcb.getId(), pendingSurvToDelete, true);
            } catch (Exception ex) {
                LOGGER.error("Error deleting pending surveillance with id " + pendingSurvToDelete, ex);
            }

            try {
                // if a surveillance was getting replaced, delete it
                if (!StringUtils.isEmpty(survToInsert.getSurveillanceIdToReplace())) {
                    Surveillance survToReplace = survManager.getByFriendlyIdAndProduct(
                            survToInsert.getCertifiedProduct().getId(), survToInsert.getSurveillanceIdToReplace());
                    CertifiedProductDTO survToReplaceOwningCp = cpManager
                            .getById(survToReplace.getCertifiedProduct().getId());
                    survManager.deleteSurveillance(survToReplaceOwningCp.getCertificationBodyId(), survToReplace);
                    responseHeaders.set("Cache-cleared", CacheNames.COLLECTIONS_LISTINGS);
                    responseHeaders.set("Cache-cleared", CacheNames.COLLECTIONS_LISTINGS);
                }
            } catch (Exception ex) {
                LOGGER.error("Deleting surveillance with id " + survToInsert.getSurveillanceIdToReplace()
                        + " as part of the replace operation failed", ex);
            }

            CertifiedProductSearchDetails afterCp = cpdetailsManager
                    .getCertifiedProductDetails(survToInsert.getCertifiedProduct().getId());
            activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, afterCp.getId(),
                    "Surveillance upload was confirmed for certified product " + afterCp.getChplProductNumber(),
                    beforeCp, afterCp);
            // query the inserted surveillance
            Surveillance result = survManager.getById(insertedSurv);
            return new ResponseEntity<Surveillance>(result, responseHeaders, HttpStatus.OK);
        }
        return null;
    }

    @ApiOperation(value = "Download surveillance as CSV.",
            notes = "Once per day, all surveillance and nonconformities are written out to CSV "
                    + "files on the CHPL servers. This method allows any user to download those files.")
    @RequestMapping(value = "/download", method = RequestMethod.GET, produces = "text/csv")
    public void download(@RequestParam(value = "type", required = false, defaultValue = "") String type,
            @RequestParam(value = "definition", defaultValue = "false", required = false) Boolean isDefinition,
            HttpServletRequest request, HttpServletResponse response) throws IOException, EntityRetrievalException {

        File downloadFile = null;
        if (isDefinition != null && isDefinition.booleanValue() == true) {
            String downloadFolderLocation = env.getProperty("downloadFolderPath");
            File downloadFolder = new File(downloadFolderLocation);
            String schemaFilename = env.getProperty("schemaSurveillanceName");
            String absolutePath = downloadFolder.getAbsolutePath() + File.separator + schemaFilename;
            if (!StringUtils.isEmpty(absolutePath)) {
                downloadFile = new File(absolutePath);
                if (!downloadFile.exists()) {
                    response.getWriter()
                            .write(String.format(messageSource.getMessage(
                                    new DefaultMessageSourceResolvable("resources.schemaFileNotFound"),
                                    LocaleContextHolder.getLocale()), absolutePath));
                    return;
                }
            }

        } else {
            try {
                if (type.equalsIgnoreCase("all")) {
                    downloadFile = survManager.getDownloadFile("surveillance-all.csv");
                } else if (type.equalsIgnoreCase("basic")) {
                    downloadFile = survManager.getProtectedDownloadFile("surveillance-basic-report.csv");
                } else {
                    downloadFile = survManager.getDownloadFile("surveillance-with-nonconformities.csv");
                }
            } catch (final IOException ex) {
                response.getWriter().append(ex.getMessage());
                return;
            }
        }

        if (downloadFile == null) {
            response.getWriter()
                    .append(String.format(messageSource.getMessage(
                            new DefaultMessageSourceResolvable("resources.schemaFileGeneralError"),
                            LocaleContextHolder.getLocale())));
            return;
        }

        LOGGER.info("Downloading " + downloadFile.getName());

        FileInputStream inputStream = new FileInputStream(downloadFile);

        // set content attributes for the response
        response.setContentType("text/csv");
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outStream.close();
    }

    @ApiOperation(value = "Upload a file with surveillance and nonconformities for certified products.",
            notes = "Accepts a CSV file with very specific fields to create pending surveillance items. "
                    + " The user uploading the file must have ROLE_ACB "
                    + " and administrative authority on the ACB(s) responsible for the product(s) in the file.")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody ResponseEntity<?> upload(@RequestParam("file") MultipartFile file)
            throws ValidationException, MaxUploadSizeExceededException, EntityRetrievalException,
            EntityCreationException {
        if (file.isEmpty()) {
            throw new ValidationException("You cannot upload an empty file!");
        }

        if (!file.getContentType().equalsIgnoreCase("text/csv")
                && !file.getContentType().equalsIgnoreCase("application/vnd.ms-excel")) {
            throw new ValidationException("File must be a CSV document.");
        }

        String surveillanceThresholdToProcessAsJobStr = env.getProperty("surveillanceThresholdToProcessAsJob").trim();
        Integer surveillanceThresholdToProcessAsJob = 50;
        try {
            surveillanceThresholdToProcessAsJob = Integer.parseInt(surveillanceThresholdToProcessAsJobStr);
        } catch (final NumberFormatException ex) {
            LOGGER.error(
                    "Could not format " + surveillanceThresholdToProcessAsJobStr + " as an integer. Defaulting to 50 instead.");
        }
        
        //first we need to count how many surveillance records are in the file
        //to know if we handle it normally or as a background job
        String data = FileUtils.readFileAsString(file);

        int numSurveillance = survUploadManager.countSurveillanceRecords(data);
        if(numSurveillance < surveillanceThresholdToProcessAsJob) {
            //process as normal
            List<Surveillance> uploadedSurveillance = new ArrayList<Surveillance>();
            List<Surveillance> pendingSurvs = survUploadManager.parseUploadFile(file);
            for (Surveillance surv : pendingSurvs) {
                CertifiedProductDTO owningCp = null;
                try {
                    owningCp = cpManager.getById(surv.getCertifiedProduct().getId());
                    survValidator.validate(surv);
                    Long pendingId = survManager.createPendingSurveillance(owningCp.getCertificationBodyId(), surv);
                    Surveillance uploaded = survManager.getPendingById(owningCp.getCertificationBodyId(), pendingId,
                            false);
                    uploadedSurveillance.add(uploaded);
                } catch (final AccessDeniedException denied) {
                    LOGGER.error(
                            "User " + Util.getCurrentUser().getSubjectName()
                                    + " does not have access to add surveillance"
                                    + (owningCp != null
                                            ? " to ACB with ID '" + owningCp.getCertificationBodyId() + "'."
                                            : "."));
                } catch (Exception ex) {
                    LOGGER.error(
                            "Error adding a new pending surveillance. Please make sure all required fields are present.",
                            ex);
                }
            }
            
            SurveillanceResults results = new SurveillanceResults();
            results.getPendingSurveillance().addAll(uploadedSurveillance);
            return new ResponseEntity<SurveillanceResults>(results, HttpStatus.OK);
        } else { //process as job
            //figure out the user
            UserDTO currentUser = null;
            try {
                currentUser = userManager.getById(Util.getCurrentUser().getId());
            } catch (final UserRetrievalException ex) {
                LOGGER.error("Error finding user with ID " + Util.getCurrentUser().getId() + ": " + ex.getMessage());
                return new ResponseEntity<Job>(HttpStatus.UNAUTHORIZED);
            }
            if (currentUser == null) {
                LOGGER.error("No user with ID " + Util.getCurrentUser().getId() + " could be found in the system.");
                return new ResponseEntity<Job>(HttpStatus.UNAUTHORIZED);
            }

            JobTypeDTO jobType = null;
            List<JobTypeDTO> jobTypes = jobManager.getAllJobTypes();
            for (JobTypeDTO jt : jobTypes) {
                if (jt.getName().equalsIgnoreCase(allowedJobType.getName())) {
                    jobType = jt;
                }
            }

            JobDTO toCreate = new JobDTO();
            toCreate.setData(data);
            toCreate.setUser(currentUser);
            toCreate.setJobType(jobType);
            JobDTO insertedJob = jobManager.createJob(toCreate);
            JobDTO createdJob = jobManager.getJobById(insertedJob.getId());

            try {
                boolean isStarted = jobManager.start(createdJob);
                if (!isStarted) {
                    return new ResponseEntity<Job>(new Job(createdJob), HttpStatus.BAD_REQUEST);
                } else {
                    createdJob = jobManager.getJobById(insertedJob.getId());
                }
            } catch (final EntityRetrievalException ex) {
                LOGGER.error("Could not mark job " + createdJob.getId() + " as started.");
                return new ResponseEntity<Job>(new Job(createdJob), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // query the now running job
            return new ResponseEntity<Job>(new Job(createdJob), HttpStatus.OK);
        }
    }

    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
