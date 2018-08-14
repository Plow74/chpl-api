package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import gov.healthit.chpl.dao.CriterionProductStatisticsDAO;
import gov.healthit.chpl.dto.CriterionProductStatisticsDTO;
import gov.healthit.chpl.entity.CriterionProductStatisticsEntity;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;

/**
 * The implementation for CriterionProductStatisticsDAO.
 * @author alarned
 *
 */
@Repository("criterionProductStatisticsDAO")
public class CriterionProductStatisticsDAOImpl extends BaseDAOImpl implements CriterionProductStatisticsDAO {
    @Override
    public List<CriterionProductStatisticsDTO> findAll() {
        List<CriterionProductStatisticsEntity> result = this.findAllEntities();
        List<CriterionProductStatisticsDTO> dtos = new ArrayList<CriterionProductStatisticsDTO>(result.size());
        for (CriterionProductStatisticsEntity entity : result) {
            dtos.add(new CriterionProductStatisticsDTO(entity));
        }
        return dtos;
    }

    @Override
    public void delete(final Long id) throws EntityRetrievalException {
        CriterionProductStatisticsEntity toDelete = getEntityById(id);

        if (toDelete != null) {
            toDelete.setDeleted(true);
            toDelete.setLastModifiedUser(getUserId(SYSTEM_USER_ID));
            entityManager.merge(toDelete);
        }
    }

    @Override
    public CriterionProductStatisticsEntity create(final CriterionProductStatisticsDTO dto)
            throws EntityCreationException, EntityRetrievalException {
        CriterionProductStatisticsEntity entity = new CriterionProductStatisticsEntity();
        entity.setProductCount(dto.getProductCount());
        entity.setCertificationCriterionId(dto.getCertificationCriterionId());

        if (dto.getDeleted() != null) {
            entity.setDeleted(dto.getDeleted());
        } else {
            entity.setDeleted(false);
        }
        if (dto.getLastModifiedUser() != null) {
            entity.setLastModifiedUser(dto.getLastModifiedUser());
        } else {
            entity.setLastModifiedUser(getUserId(SYSTEM_USER_ID));
        }
        if (dto.getLastModifiedDate() != null) {
            entity.setLastModifiedDate(dto.getLastModifiedDate());
        } else {
            entity.setLastModifiedDate(new Date());
        }
        if (dto.getCreationDate() != null) {
            entity.setCreationDate(dto.getCreationDate());
        } else {
            entity.setCreationDate(new Date());
        }

        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    private List<CriterionProductStatisticsEntity> findAllEntities() {
        Query query = entityManager.createQuery("from CriterionProductStatisticsEntity cpse "
                + "LEFT OUTER JOIN FETCH cpse.certificationCriterion cce "
                + "LEFT OUTER JOIN FETCH cce.certificationEdition "
                + "WHERE (cpse.deleted = false)",
                CriterionProductStatisticsEntity.class);
        return query.getResultList();
    }

    private CriterionProductStatisticsEntity getEntityById(final Long id) throws EntityRetrievalException {
        CriterionProductStatisticsEntity entity = null;

        Query query = entityManager.createQuery("from CriterionProductStatisticsEntity cpse "
                + "LEFT OUTER JOIN FETCH cpse.certificationCriterion cce "
                + "LEFT OUTER JOIN FETCH cce.certificationEdition "
                + "WHERE (cpse.deleted = false) AND (id = :entityid)",
                CriterionProductStatisticsEntity.class);
        query.setParameter("entityid", id);
        List<CriterionProductStatisticsEntity> result = query.getResultList();

        if (result.size() == 1) {
            entity = result.get(0);
        } else {
            throw new EntityRetrievalException("Data error. Did not find only one entity.");
        }

        return entity;
    }
}
