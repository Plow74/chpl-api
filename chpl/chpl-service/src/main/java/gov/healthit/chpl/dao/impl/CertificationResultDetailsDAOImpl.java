package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.healthit.chpl.dao.CertificationResultDetailsDAO;
import gov.healthit.chpl.dto.CertificationResultDetailsDTO;
import gov.healthit.chpl.entity.listing.CertificationResultDetailsEntity;
import gov.healthit.chpl.exception.EntityRetrievalException;

@Repository(value = "certificationResultDetailsDAO")
public class CertificationResultDetailsDAOImpl extends BaseDAOImpl implements CertificationResultDetailsDAO {

    @Override
    @Transactional
    public List<CertificationResultDetailsDTO> getCertificationResultDetailsByCertifiedProductId(
            final Long certifiedProductId) throws EntityRetrievalException {
        List<CertificationResultDetailsEntity> entities = getEntitiesByCertifiedProductId(certifiedProductId);
        List<CertificationResultDetailsDTO> dtos = new ArrayList<CertificationResultDetailsDTO>(entities.size());

        for (CertificationResultDetailsEntity entity : entities) {
            dtos.add(new CertificationResultDetailsDTO(entity));
        }
        return dtos;
    }

    private List<CertificationResultDetailsEntity> getEntitiesByCertifiedProductId(final Long productId)
            throws EntityRetrievalException {

        CertificationResultDetailsEntity entity = null;

        Query query = entityManager.createQuery(
                "from CertificationResultDetailsEntity where (NOT deleted = true) AND (certified_product_id = :entityid) ",
                CertificationResultDetailsEntity.class);
        query.setParameter("entityid", productId);
        List<CertificationResultDetailsEntity> result = query.getResultList();

        return result;
    }

    @Override
    public List<CertificationResultDetailsDTO> getCertificationResultDetailsByCertifiedProductIdSED(
            final Long certifiedProductId) throws EntityRetrievalException {
        List<CertificationResultDetailsEntity> entities = getEntitiesByCertifiedProductIdSED(certifiedProductId);
        List<CertificationResultDetailsDTO> dtos = new ArrayList<CertificationResultDetailsDTO>(entities.size());

        for (CertificationResultDetailsEntity entity : entities) {
            dtos.add(new CertificationResultDetailsDTO(entity));
        }
        return dtos;
    }

    private List<CertificationResultDetailsEntity> getEntitiesByCertifiedProductIdSED(final Long productId)
            throws EntityRetrievalException {

        CertificationResultDetailsEntity entity = null;

        Query query = entityManager.createQuery(
                "from CertificationResultDetailsEntity where (NOT deleted = true) AND (certified_product_id = :entityid) AND (success = true) AND (sed = true) ",
                CertificationResultDetailsEntity.class);
        query.setParameter("entityid", productId);
        List<CertificationResultDetailsEntity> result = query.getResultList();

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResultDetailsDTO> getByUrl(final String url) {
        String queryStr = "SELECT cr "
                + "FROM CertificationResultDetailsEntity cr "
                + "WHERE cr.deleted = false "
                + "AND cr.apiDocumentation = :url";

        Query query = entityManager.createQuery(queryStr, CertificationResultDetailsEntity.class);
        query.setParameter("url", url);
        List<CertificationResultDetailsEntity> entities = query.getResultList();
        List<CertificationResultDetailsDTO> resultDtos = new ArrayList<CertificationResultDetailsDTO>();
        for (CertificationResultDetailsEntity entity : entities) {
            resultDtos.add(new CertificationResultDetailsDTO(entity));
        }
        return resultDtos;
    }

}
