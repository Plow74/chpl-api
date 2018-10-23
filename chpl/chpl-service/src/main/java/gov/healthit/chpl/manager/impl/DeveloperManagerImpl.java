package gov.healthit.chpl.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.caching.CacheNames;
import gov.healthit.chpl.dao.CertificationBodyDAO;
import gov.healthit.chpl.dao.CertifiedProductDAO;
import gov.healthit.chpl.dao.DeveloperDAO;
import gov.healthit.chpl.domain.CertificationBody;
import gov.healthit.chpl.domain.DecertifiedDeveloperResult;
import gov.healthit.chpl.domain.DeveloperTransparency;
import gov.healthit.chpl.domain.concept.ActivityConcept;
import gov.healthit.chpl.dto.CertificationBodyDTO;
import gov.healthit.chpl.dto.CertifiedProductDetailsDTO;
import gov.healthit.chpl.dto.DecertifiedDeveloperDTO;
import gov.healthit.chpl.dto.DeveloperACBMapDTO;
import gov.healthit.chpl.dto.DeveloperDTO;
import gov.healthit.chpl.dto.DeveloperStatusEventDTO;
import gov.healthit.chpl.dto.DeveloperStatusEventPair;
import gov.healthit.chpl.dto.ProductDTO;
import gov.healthit.chpl.dto.ProductOwnerDTO;
import gov.healthit.chpl.entity.AttestationType;
import gov.healthit.chpl.entity.developer.DeveloperStatusType;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.exception.MissingReasonException;
import gov.healthit.chpl.exception.ValidationException;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.CertificationBodyManager;
import gov.healthit.chpl.manager.DeveloperManager;
import gov.healthit.chpl.manager.ProductManager;
import gov.healthit.chpl.util.ChplProductNumberUtil;
import gov.healthit.chpl.util.ErrorMessageUtil;
import gov.healthit.chpl.util.ValidationUtils;

@Service
public class DeveloperManagerImpl implements DeveloperManager {
    private static final Logger LOGGER = LogManager.getLogger(DeveloperManagerImpl.class);

    @Autowired
    private DeveloperDAO developerDao;

    @Autowired
    private ProductManager productManager;

    @Autowired
    private CertificationBodyManager acbManager;

    @Autowired
    private CertificationBodyDAO certificationBodyDao;

    @Autowired
    private CertifiedProductDAO certifiedProductDAO;

    @Autowired
    private ChplProductNumberUtil chplProductNumberUtil;

    @Autowired
    private ActivityManager activityManager;

    @Autowired private ErrorMessageUtil msgUtil;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(CacheNames.ALL_DEVELOPERS)
    public List<DeveloperDTO> getAll() {
        List<DeveloperDTO> allDevelopers = developerDao.findAll();
        List<DeveloperDTO> allDevelopersWithTransparencies = addTransparencyMappings(allDevelopers);
        return allDevelopersWithTransparencies;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB')")
    @Cacheable(CacheNames.ALL_DEVELOPERS_INCLUDING_DELETED)
    public List<DeveloperDTO> getAllIncludingDeleted() {
        List<DeveloperDTO> allDevelopers = developerDao.findAllIncludingDeleted();
        List<DeveloperDTO> allDevelopersWithTransparencies = addTransparencyMappings(allDevelopers);
        return allDevelopersWithTransparencies;
    }

    @Override
    //@Transactional(readOnly = true)
    public DeveloperDTO getById(final Long id) throws EntityRetrievalException {
        DeveloperDTO developer = developerDao.getById(id);
        List<CertificationBodyDTO> availableAcbs = acbManager.getAllForUser(false);
        if (availableAcbs == null || availableAcbs.size() == 0) {
            availableAcbs = acbManager.getAll(true);
        }
        // someone will see either the transparencies that apply to the ACBs to
        // which they have access
        // or they will see the transparencies for all ACBs if they are an admin
        // or not logged in
        for (CertificationBodyDTO acb : availableAcbs) {
            DeveloperACBMapDTO map = developerDao.getTransparencyMapping(developer.getId(), acb.getId());
            if (map == null) {
                DeveloperACBMapDTO mapToAdd = new DeveloperACBMapDTO();
                mapToAdd.setAcbId(acb.getId());
                mapToAdd.setAcbName(acb.getName());
                mapToAdd.setDeveloperId(developer.getId());
                mapToAdd.setTransparencyAttestation(null);
                developer.getTransparencyAttestationMappings().add(mapToAdd);
            } else {
                map.setAcbName(acb.getName());
                developer.getTransparencyAttestationMappings().add(map);
            }
        }

        //Remove any deleted status events
        Iterator<DeveloperStatusEventDTO> iterator = developer.getStatusEvents().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getDeleted()) {
                iterator.remove();
            }
        }
        return developer;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(CacheNames.COLLECTIONS_DEVELOPERS)
    public List<DeveloperTransparency> getDeveloperCollection() {
        return developerDao.getAllDevelopersWithTransparencies();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB')")
    @Transactional(readOnly = false)
    @CacheEvict(value = {
            CacheNames.ALL_DEVELOPERS, CacheNames.ALL_DEVELOPERS_INCLUDING_DELETED, CacheNames.DEVELOPER_NAMES,
            CacheNames.COLLECTIONS_DEVELOPERS, CacheNames.GET_DECERTIFIED_DEVELOPERS
    }, allEntries = true)
    public DeveloperDTO update(final DeveloperDTO updatedDev)
            throws EntityRetrievalException, JsonProcessingException,
            EntityCreationException, MissingReasonException {

        DeveloperDTO beforeDev = getById(updatedDev.getId());
        DeveloperStatusEventDTO newDevStatus = updatedDev.getStatus();
        DeveloperStatusEventDTO currDevStatus = beforeDev.getStatus();
        if (currDevStatus == null || currDevStatus.getStatus() == null) {
            String msg = msgUtil.getMessage("developer.noStatusFound", beforeDev.getName());
            LOGGER.error(msg);
            throw new EntityCreationException(msg);
        }

        //if any of the statuses (new, old, or any other status in the history)
        //is Under Certification Ban by ONC make sure there is a reason given
        LOGGER.info("Original Developer Status Event count = " + beforeDev.getStatusEvents().size());
        LOGGER.info("Updated Developer Status Event count = " + updatedDev.getStatusEvents().size());
        for (DeveloperStatusEventDTO statusEvent: updatedDev.getStatusEvents()) {
            if (statusEvent.getStatus().getStatusName().equals(
                    DeveloperStatusType.UnderCertificationBanByOnc.toString())
                    && StringUtils.isEmpty(statusEvent.getReason())) {
                throw new MissingReasonException(msgUtil.getMessage("developer.missingReasonForBan",
                        DeveloperStatusType.UnderCertificationBanByOnc.toString()));
            }
        }

        // if the before status is not Active and the user is not ROLE_ADMIN
        // then nothing can be changed
        if (!currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())
                && !Util.isUserRoleAdmin()) {
            String msg = msgUtil.getMessage("developer.notActiveNotAdminCantChangeStatus",
                    Util.getUsername(), beforeDev.getName());
            LOGGER.error(msg);
            throw new EntityCreationException(msg);
        }

        // if the status history has been modified, the user must be role admin
        // except that an acb admin can change to UnderCertificationBanByOnc
        // triggered by listing status update
        boolean devStatusHistoryUpdated = isStatusHistoryUpdated(beforeDev, updatedDev);
        if (devStatusHistoryUpdated
                && newDevStatus.getStatus().getStatusName()
                .equals(DeveloperStatusType.UnderCertificationBanByOnc.toString())
                && !Util.isUserRoleAdmin()) {
            String  msg = msgUtil.getMessage("developer.statusChangeNotAllowedWithoutAdmin",
                    DeveloperStatusType.UnderCertificationBanByOnc.toString());
            throw new EntityCreationException(msg);
        } else if (devStatusHistoryUpdated && !newDevStatus.getStatus().getStatusName()
                .equals(DeveloperStatusType.UnderCertificationBanByOnc.toString()) && !Util.isUserRoleAdmin()) {
            String msg = msgUtil.getMessage("developer.statusHistoryChangeNotAllowedWithoutAdmin");
            throw new EntityCreationException(msg);
        }

        // determine if the status has been changed
        // in most cases only allowed by ROLE_ADMIN but ROLE_ACB
        // can change it to UnderCertificationBanByOnc
        boolean currentStatusChanged = !currDevStatus.getStatus().getStatusName()
                .equals(newDevStatus.getStatus().getStatusName());
        if (currentStatusChanged
                && newDevStatus.getStatus().getStatusName()
                .equals(DeveloperStatusType.UnderCertificationBanByOnc.toString())
                && !(Util.isUserRoleAdmin() || Util.isUserRoleAcbAdmin())) {
            String msg = msgUtil.getMessage("developer.statusChangeNotAllowedWithoutAdminOrAcb",
                    DeveloperStatusType.UnderCertificationBanByOnc.toString());
            throw new EntityCreationException(msg);
        } else if (currentStatusChanged && !newDevStatus.getStatus().getStatusName()
                .equals(DeveloperStatusType.UnderCertificationBanByOnc.toString()) && !Util.isUserRoleAdmin()) {
            String  msg = msgUtil.getMessage("developer.statusChangeNotAllowedWithoutAdmin");
            throw new EntityCreationException(msg);
        } else if (!currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())
                && !newDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())) {
            // if the developer is not active and not going to be active
            // only its current status can be updated
            updateStatusHistory(beforeDev, updatedDev);
        } else {
            /*
             * Check to see that the Developer's website is valid.
             */
            if (!StringUtils.isEmpty(updatedDev.getWebsite())) {
                if (!ValidationUtils.isWellFormedUrl(updatedDev.getWebsite())) {
                    String msg = msgUtil.getMessage("developer.websiteIsInvalid");
                    throw new EntityCreationException(msg);
                }
            }
    
            if (beforeDev.getContact() != null && beforeDev.getContact().getId() != null) {
                updatedDev.getContact().setId(beforeDev.getContact().getId());
            }
    
            // if either the before or updated statuses are active and the user is
            // ROLE_ADMIN
            // OR if before status is active and user is not ROLE_ADMIN - proceed
            if (((currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())
                    || newDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString()))
                    && Util.isUserRoleAdmin())
                    || (currDevStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())
                            && !Util.isUserRoleAdmin())) {
    
                developerDao.update(updatedDev);
                
                updateStatusHistory(beforeDev, updatedDev);
                
                List<CertificationBodyDTO> availableAcbs = acbManager.getAllForUser(false);
                if (availableAcbs != null && availableAcbs.size() > 0) {
                    for (CertificationBodyDTO acb : availableAcbs) {
                        DeveloperACBMapDTO existingMap = developerDao.getTransparencyMapping(updatedDev.getId(),
                                acb.getId());
                        if (existingMap == null) {
                            DeveloperACBMapDTO developerMappingToCreate = new DeveloperACBMapDTO();
                            developerMappingToCreate.setAcbId(acb.getId());
                            developerMappingToCreate.setDeveloperId(beforeDev.getId());
                            for (DeveloperACBMapDTO attMap : updatedDev.getTransparencyAttestationMappings()) {
                                if (attMap.getAcbName().equals(acb.getName())) {
                                    developerMappingToCreate
                                    .setTransparencyAttestation(attMap.getTransparencyAttestation());
                                    developerDao.createTransparencyMapping(developerMappingToCreate);
                                }
                            }
                        } else {
                            for (DeveloperACBMapDTO attMap : updatedDev.getTransparencyAttestationMappings()) {
                                if (attMap.getAcbName().equals(acb.getName())) {
                                    existingMap.setTransparencyAttestation(attMap.getTransparencyAttestation());
                                    developerDao.updateTransparencyMapping(existingMap);
                                }
                            }
                        }
                    }
                }
            }
        }
        DeveloperDTO after = getById(updatedDev.getId());
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_DEVELOPER, after.getId(),
                "Developer " + updatedDev.getName() + " was updated.", beforeDev, after);
        return after;
    }

    private void updateStatusHistory(final DeveloperDTO beforeDev, final DeveloperDTO updatedDev)
            throws EntityRetrievalException, EntityCreationException {
        //update status history
        List<DeveloperStatusEventDTO> statusEventsToAdd = new ArrayList<DeveloperStatusEventDTO>();
        List<DeveloperStatusEventPair> statusEventsToUpdate = new ArrayList<DeveloperStatusEventPair>();
        List<DeveloperStatusEventDTO> statusEventsToRemove = new ArrayList<DeveloperStatusEventDTO>();

        statusEventsToUpdate = DeveloperStatusEventsHelper.getUpdatedEvents(beforeDev.getStatusEvents(), updatedDev.getStatusEvents());
        statusEventsToRemove = DeveloperStatusEventsHelper.getRemovedEvents(beforeDev.getStatusEvents(), updatedDev.getStatusEvents());
        statusEventsToAdd = DeveloperStatusEventsHelper.getAddedEvents(beforeDev.getStatusEvents(), updatedDev.getStatusEvents());

        for (DeveloperStatusEventPair toUpdate : statusEventsToUpdate) {
            boolean hasChanged = false;
            if (!ObjectUtils.equals(toUpdate.getOrig().getStatusDate(),
                    toUpdate.getUpdated().getStatusDate())
                    || !ObjectUtils.equals(toUpdate.getOrig().getStatus().getId(),
                            toUpdate.getUpdated().getStatus().getId())
                    || !ObjectUtils.equals(toUpdate.getOrig().getStatus().getStatusName(),
                            toUpdate.getUpdated().getStatus().getStatusName())
                    || !ObjectUtils.equals(toUpdate.getOrig().getReason(),
                            toUpdate.getUpdated().getReason())) {
                hasChanged = true;
            }

            if (hasChanged) {
                DeveloperStatusEventDTO dseToUpdate = toUpdate.getUpdated();
                dseToUpdate.setDeveloperId(beforeDev.getId());
                LOGGER.info("Status Events Id to Update: " + dseToUpdate.getId());
                developerDao.updateDeveloperStatusEvent(dseToUpdate);
            }
        }

        LOGGER.info("Status Events to Add count: " + statusEventsToAdd.size());
        for (DeveloperStatusEventDTO toAdd : statusEventsToAdd) {
            toAdd.setDeveloperId(beforeDev.getId());
            developerDao.createDeveloperStatusEvent(toAdd);
        }

        LOGGER.info("Status Events to Remove count: " + statusEventsToRemove.size());
        for (DeveloperStatusEventDTO toRemove : statusEventsToRemove) {
            developerDao.deleteDeveloperStatusEvent(toRemove);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB')")
    @Transactional(readOnly = false)
    @CacheEvict(value = {
            CacheNames.ALL_DEVELOPERS, CacheNames.ALL_DEVELOPERS_INCLUDING_DELETED, CacheNames.DEVELOPER_NAMES,
            CacheNames.COLLECTIONS_DEVELOPERS
    }, allEntries = true)
    public DeveloperDTO create(final DeveloperDTO dto)
            throws EntityRetrievalException, EntityCreationException, JsonProcessingException {

        /*
         * Check to see that the Developer's website is valid.
         */
        if (!StringUtils.isEmpty(dto.getWebsite())) {
            if (!ValidationUtils.isWellFormedUrl(dto.getWebsite())) {
                String msg = msgUtil.getMessage("developer.websiteIsInvalid");
                throw new EntityCreationException(msg);
            }
        }

        DeveloperDTO created = developerDao.create(dto);

        List<CertificationBodyDTO> availableAcbs = acbManager.getAllForUser(false);
        if (availableAcbs != null && availableAcbs.size() > 0) {
            for (CertificationBodyDTO acb : availableAcbs) {
                for (DeveloperACBMapDTO attMap : dto.getTransparencyAttestationMappings()) {
                    if (acb.getId().longValue() == attMap.getAcbId().longValue()
                            && !StringUtils.isEmpty(attMap.getTransparencyAttestation())) {
                        attMap.setDeveloperId(created.getId());
                        developerDao.createTransparencyMapping(attMap);
                    }
                }
            }
        }
        activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_DEVELOPER, created.getId(),
                "Developer " + created.getName() + " has been created.", null, created);
        return created;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = false)
    @CacheEvict(value = {
            CacheNames.ALL_DEVELOPERS, CacheNames.ALL_DEVELOPERS_INCLUDING_DELETED, CacheNames.DEVELOPER_NAMES,
            CacheNames.COLLECTIONS_DEVELOPERS, CacheNames.GET_DECERTIFIED_DEVELOPERS
    }, allEntries = true)
    public DeveloperDTO merge(final List<Long> developerIdsToMerge, final DeveloperDTO developerToCreate)
            throws EntityRetrievalException, JsonProcessingException, EntityCreationException, ValidationException {

        List<DeveloperDTO> beforeDevelopers = new ArrayList<DeveloperDTO>();
        for (Long developerId : developerIdsToMerge) {
            beforeDevelopers.add(developerDao.getById(developerId));
        }

        //Check to see if the merge will create any duplicate chplProductNumbers
        List<DuplicateChplProdNumber> duplicateChplProdNumbers =
                getDuplicateChplProductNumbersBasedOnDevMerge(developerIdsToMerge,
                        developerToCreate.getDeveloperCode());
        if (duplicateChplProdNumbers.size() != 0) {
            throw new ValidationException(
                    getDuplicateChplProductNumberErrorMessages(duplicateChplProdNumbers), null);
        }

        // check for any non-active developers and throw an error if any are
        // found
        for (DeveloperDTO beforeDeveloper : beforeDevelopers) {
            DeveloperStatusEventDTO currDeveloperStatus = beforeDeveloper.getStatus();
            if (currDeveloperStatus == null || currDeveloperStatus.getStatus() == null) {
                String msg = "Cannot merge developer " + beforeDeveloper.getName()
                + " because their current status cannot be determined.";
                LOGGER.error(msg);
                throw new EntityCreationException(msg);
            } else if (!currDeveloperStatus.getStatus().getStatusName().equals(DeveloperStatusType.Active.toString())) {
                String msg = "Cannot merge developer " + beforeDeveloper.getName() + " with a status of "
                        + currDeveloperStatus.getStatus().getStatusName();
                LOGGER.error(msg);
                throw new EntityCreationException(msg);
            }
        }

        // check if the transparency attestation for each developer is
        // conflicting
        List<CertificationBodyDTO> allAcbs = acbManager.getAll(false);
        for (CertificationBodyDTO acb : allAcbs) {
            AttestationType transparencyAttestation = null;
            for (DeveloperDTO dev : beforeDevelopers) {
                DeveloperACBMapDTO taMap = developerDao.getTransparencyMapping(dev.getId(), acb.getId());
                if (taMap != null && !StringUtils.isEmpty(taMap.getTransparencyAttestation())) {
                    AttestationType currAtt = AttestationType.getValue(taMap.getTransparencyAttestation());
                    if (transparencyAttestation == null) {
                        transparencyAttestation = currAtt;
                    } else if (currAtt != transparencyAttestation) {
                        throw new EntityCreationException("Cannot complete merge because " + acb.getName()
                        + " has a conflicting transparency attestation for these developers.");
                    }
                }
            }

            if (transparencyAttestation != null) {
                DeveloperACBMapDTO devMap = new DeveloperACBMapDTO();
                devMap.setAcbId(acb.getId());
                devMap.setAcbName(acb.getName());
                devMap.setTransparencyAttestation(transparencyAttestation.name());
                developerToCreate.getTransparencyAttestationMappings().add(devMap);
            }
        }

        DeveloperDTO createdDeveloper = create(developerToCreate);
        // search for any products assigned to the list of developers passed in
        List<ProductDTO> developerProducts = productManager.getByDevelopers(developerIdsToMerge);
        for (ProductDTO product : developerProducts) {
            // add an item to the ownership history of each product
            ProductOwnerDTO historyToAdd = new ProductOwnerDTO();
            historyToAdd.setProductId(product.getId());
            DeveloperDTO prevOwner = new DeveloperDTO();
            prevOwner.setId(product.getDeveloperId());
            historyToAdd.setDeveloper(prevOwner);
            historyToAdd.setTransferDate(System.currentTimeMillis());
            product.getOwnerHistory().add(historyToAdd);
            // reassign those products to the new developer
            product.setDeveloperId(createdDeveloper.getId());
            productManager.update(product);

        }
        // - mark the passed in developers as deleted
        for (Long developerId : developerIdsToMerge) {
            List<CertificationBodyDTO> availableAcbs = acbManager.getAllForUser(false);
            if (availableAcbs != null && availableAcbs.size() > 0) {
                for (CertificationBodyDTO acb : availableAcbs) {
                    developerDao.deleteTransparencyMapping(developerId, acb.getId());
                }
            }
            developerDao.delete(developerId);
        }

        activityManager
        .addActivity(ActivityConcept.ACTIVITY_CONCEPT_DEVELOPER,
                createdDeveloper.getId(), "Merged " + developerIdsToMerge.size()
                + " developers into new developer '" + createdDeveloper.getName() + "'.",
                beforeDevelopers, createdDeveloper);

        return createdDeveloper;
    }

    public static List<DeveloperStatusEventDTO> cloneDeveloperStatusEventList(List<DeveloperStatusEventDTO> original) {
        List<DeveloperStatusEventDTO> clone = new ArrayList<DeveloperStatusEventDTO>();
        for (DeveloperStatusEventDTO event : original) {
            clone.add(new DeveloperStatusEventDTO(event));
        }
        return clone;
    }

    private Set<String> getDuplicateChplProductNumberErrorMessages(
            final List<DuplicateChplProdNumber> duplicateChplProdNumbers) {

        Set<String> messages = new HashSet<String>();

        for (DuplicateChplProdNumber dup : duplicateChplProdNumbers) {
            messages.add(msgUtil.getMessage("developer.merge.dupChplProdNbrs",
                    dup.getOrigChplProductNumberA(),
                    dup.getOrigChplProductNumberB()));
        }
        return messages;
    }

    private List<DuplicateChplProdNumber> getDuplicateChplProductNumbersBasedOnDevMerge(
            final List<Long> developerIds, final String newDeveloperCode) {

        //key = new chpl prod nbr, value = orig chpl prod nbr
        HashMap<String, String> newChplProductNumbers = new HashMap<String, String>();

        String newChplProductNumber = "";

        //Hold the list of duplicate chpl prod nbrs {new, origA, origB} where "origA" and "origB" are the
        //original chpl prod nbrs that would be duplicated during merge and "new" is chpl prod nbr that
        // "origA" and "origB" would be updated to
        List<DuplicateChplProdNumber> duplicatedChplProductNumbers =
                new ArrayList<DuplicateChplProdNumber>();

        for (Long developerId : developerIds) {
            List<CertifiedProductDetailsDTO> certifiedProducts =
                    certifiedProductDAO.findByDeveloperId(developerId);

            for (CertifiedProductDetailsDTO certifiedProduct : certifiedProducts) {
                newChplProductNumber = "";
                if (certifiedProduct.getChplProductNumber().startsWith("CHP")) {
                    newChplProductNumber = certifiedProduct.getChplProductNumber();
                } else {
                    newChplProductNumber = chplProductNumberUtil.getChplProductNumber(certifiedProduct.getYear(),
                            chplProductNumberUtil.parseChplProductNumber(
                                    certifiedProduct.getChplProductNumber()).getAtlCode(),
                            certifiedProduct.getCertificationBodyCode(),
                            newDeveloperCode,
                            certifiedProduct.getProductCode(),
                            certifiedProduct.getVersionCode(),
                            certifiedProduct.getIcsCode(),
                            certifiedProduct.getAdditionalSoftwareCode(),
                            certifiedProduct.getCertifiedDateCode());
                }
                if (newChplProductNumbers.containsKey(newChplProductNumber)) {
                    duplicatedChplProductNumbers.add(
                            new DuplicateChplProdNumber(
                                    newChplProductNumbers.get(newChplProductNumber),
                                    certifiedProduct.getChplProductNumber(),
                                    newChplProductNumber));
                } else {
                    newChplProductNumbers.put(newChplProductNumber, certifiedProduct.getChplProductNumber());
                }
            }
        }
        return duplicatedChplProductNumbers;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(CacheNames.GET_DECERTIFIED_DEVELOPERS)
    public List<DecertifiedDeveloperResult> getDecertifiedDevelopers() throws EntityRetrievalException {
        List<DecertifiedDeveloperDTO> dtoList = new ArrayList<DecertifiedDeveloperDTO>();
        List<DecertifiedDeveloperResult> decertifiedDeveloperResults = new ArrayList<DecertifiedDeveloperResult>();

        dtoList = developerDao.getDecertifiedDevelopers();

        for (DecertifiedDeveloperDTO dto : dtoList) {
            List<CertificationBody> certifyingBody = new ArrayList<CertificationBody>();
            for (Long oncacbId : dto.getAcbIdList()) {
                CertificationBody cb = new CertificationBody(certificationBodyDao.getById(oncacbId));
                certifyingBody.add(cb);
            }

            DecertifiedDeveloperResult decertifiedDeveloper = new DecertifiedDeveloperResult(
                    developerDao.getById(dto.getDeveloperId()), certifyingBody, dto.getDecertificationDate(),
                    dto.getNumMeaningfulUse(), dto.getEarliestNumMeaningfulUseDate(), dto.getLatestNumMeaningfulUseDate());
            decertifiedDeveloperResults.add(decertifiedDeveloper);
        }
        return decertifiedDeveloperResults;
    }

    private boolean isStatusHistoryUpdated(final DeveloperDTO original, final DeveloperDTO changed) {
        boolean hasChanged = false;
        if ((original.getStatusEvents() != null && changed.getStatusEvents() == null)
                || (original.getStatusEvents() == null && changed.getStatusEvents() != null)
                || (original.getStatusEvents().size() != changed.getStatusEvents().size())) {
            hasChanged = true;
        } else {
            // neither status history is null and they have the same size
            // history arrays
            // so now check for any differences in the values of each
            for (DeveloperStatusEventDTO origStatusHistory : original.getStatusEvents()) {
                boolean foundMatchInChanged = false;
                for (DeveloperStatusEventDTO changedStatusHistory : changed.getStatusEvents()) {
                    if (origStatusHistory.getStatus().getId() != null
                            && changedStatusHistory.getStatus().getId() != null
                            && origStatusHistory.getStatus().getId().equals(changedStatusHistory.getStatus().getId())
                            && origStatusHistory.getStatusDate().getTime()
                                == changedStatusHistory.getStatusDate().getTime()) {
                        foundMatchInChanged = true;
                    }
                }
                hasChanged = hasChanged || !foundMatchInChanged;
            }
        }
        return hasChanged;
    }

    private List<DeveloperDTO> addTransparencyMappings(final List<DeveloperDTO> developers) {
        List<DeveloperACBMapDTO> transparencyMaps = developerDao.getAllTransparencyMappings();
        Map<Long, DeveloperDTO> mappedDevelopers = new HashMap<Long, DeveloperDTO>();
        for (DeveloperDTO dev : developers) {
            mappedDevelopers.put(dev.getId(), dev);
        }
        for (DeveloperACBMapDTO map : transparencyMaps) {
            if (map.getAcbId() != null) {
                mappedDevelopers.get(map.getDeveloperId()).getTransparencyAttestationMappings().add(map);
            }
        }
        List<DeveloperDTO> ret = new ArrayList<DeveloperDTO>();
        for (DeveloperDTO dev : mappedDevelopers.values()) {
            ret.add(dev);
        }
        return ret;
    }

    private class DuplicateChplProdNumber {
        private String origChplProductNumberA;
        private String origChplProductNumberB;

        public DuplicateChplProdNumber(final String origChplProductNumberA, final String origChplProductNumberB,
                final String newChplProductNumber) {
            this.origChplProductNumberA = origChplProductNumberA;
            this.origChplProductNumberB = origChplProductNumberB;
        }

        public String getOrigChplProductNumberA() {
            return origChplProductNumberA;
        }

        public String getOrigChplProductNumberB() {
            return origChplProductNumberB;
        }

        @Override
        public String toString() {
            return msgUtil.getMessage("developer.merge.dupChplProdNbrs.duplicate",
                    origChplProductNumberA,
                    origChplProductNumberB);
        }
    }
}
