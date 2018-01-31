package gov.healthit.chpl.manager;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.domain.IcsFamilyTreeNode;
import gov.healthit.chpl.domain.ListingUpdateRequest;
import gov.healthit.chpl.domain.MeaningfulUseUser;
import gov.healthit.chpl.dto.CertifiedProductDTO;
import gov.healthit.chpl.dto.CertifiedProductDetailsDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.web.controller.InvalidArgumentsException;
import gov.healthit.chpl.web.controller.results.MeaningfulUseUserResults;

public interface CertifiedProductManager {

    CertifiedProductDTO getById(Long id) throws EntityRetrievalException;

    CertifiedProductDTO getByChplProductNumber(String chplProductNumber) throws EntityRetrievalException;

    boolean chplIdExists(String id) throws EntityRetrievalException;

    List<CertifiedProductDetailsDTO> getDetailsByIds(List<Long> ids) throws EntityRetrievalException;

    List<CertifiedProductDetailsDTO> getAll();

    List<CertifiedProductDetailsDTO> getAllWithEditPermission();

    List<CertifiedProductDetailsDTO> getByProduct(Long productId) throws EntityRetrievalException;

    List<CertifiedProductDetailsDTO> getByVersion(Long versionId) throws EntityRetrievalException;

    List<CertifiedProductDetailsDTO> getByVersionWithEditPermission(Long versionId)
            throws EntityRetrievalException;

    CertifiedProductDTO changeOwnership(Long certifiedProductId, Long acbId)
            throws EntityRetrievalException, JsonProcessingException, EntityCreationException;

    CertifiedProductDTO update(Long acbId, ListingUpdateRequest updateRequest,
            CertifiedProductSearchDetails existingListing) throws EntityRetrievalException, JsonProcessingException,
            EntityCreationException, InvalidArgumentsException, IOException;

    void sanitizeUpdatedListingData(Long acbId, CertifiedProductSearchDetails listing)
            throws EntityNotFoundException;

    MeaningfulUseUserResults updateMeaningfulUseUsers(Set<MeaningfulUseUser> meaningfulUseUserSet)
            throws EntityCreationException, EntityRetrievalException, JsonProcessingException, IOException;

    CertifiedProductDTO createFromPending(Long acbId, PendingCertifiedProductDTO pendingCp)
            throws EntityRetrievalException, EntityCreationException, JsonProcessingException, IOException;

    List<IcsFamilyTreeNode> getIcsFamilyTree(Long certifiedProductId) throws EntityRetrievalException;

    CertifiedProductDetailsDTO getDetailsById(Long ids) throws EntityRetrievalException;
}
