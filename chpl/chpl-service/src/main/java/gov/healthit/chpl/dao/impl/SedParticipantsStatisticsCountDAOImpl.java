package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.SedParticipantStatisticsCountDAO;
import gov.healthit.chpl.dto.SedParticipantStatisticsCountDTO;
import gov.healthit.chpl.entity.SedParticipantStatisticsCountEntity;

/**
 * The implementation for SedParticipantStatisticsCountDAO.
 * @author TYoung
 *
 */
@Repository("sedParticipantStatisticsCountDAO")
public class SedParticipantsStatisticsCountDAOImpl extends BaseDAOImpl implements SedParticipantStatisticsCountDAO {
    private static final long MODIFIED_USER_ID = -3L;

    @Override
    public List<SedParticipantStatisticsCountDTO> findAll() {
        List<SedParticipantStatisticsCountEntity> result = this.findAllEntities();
        List<SedParticipantStatisticsCountDTO> dtos = new ArrayList<SedParticipantStatisticsCountDTO>(result.size());
        for (SedParticipantStatisticsCountEntity entity : result) {
            dtos.add(new SedParticipantStatisticsCountDTO(entity));
        }
        return dtos;
    }

    @Override
    public void delete(final Long id) throws EntityRetrievalException {
        SedParticipantStatisticsCountEntity toDelete = getEntityById(id);

        if (toDelete != null) {
            toDelete.setDeleted(true);
            toDelete.setLastModifiedUser(getUserId());
            entityManager.merge(toDelete);
        }
    }

    @Override
    public SedParticipantStatisticsCountEntity create(final SedParticipantStatisticsCountDTO dto)
            throws EntityCreationException, EntityRetrievalException {

        SedParticipantStatisticsCountEntity entity = new SedParticipantStatisticsCountEntity();
        entity.setSedCount(dto.getSedCount());
        entity.setParticipantCount(dto.getParticipantCount());

        if (dto.getDeleted() != null) {
            entity.setDeleted(dto.getDeleted());
        } else {
            entity.setDeleted(false);
        }

        if (dto.getLastModifiedUser() != null) {
            entity.setLastModifiedUser(dto.getLastModifiedUser());
        } else {
            entity.setLastModifiedUser(getUserId());
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

    private SedParticipantStatisticsCountEntity getEntityById(final Long id) throws EntityRetrievalException {
        SedParticipantStatisticsCountEntity entity = null;

        Query query = entityManager.createQuery(
                "from SedParticipantStatisticsCountEntity a where (NOT deleted = true) AND (id = :entityid) ",
                SedParticipantStatisticsCountEntity.class);
        query.setParameter("entityid", id);
        List<SedParticipantStatisticsCountEntity> result = query.getResultList();

        if (result.size() > 1) {
            throw new EntityRetrievalException("Data error. Duplicate address id in database.");
        } else if (result.size() == 1) {
            entity = result.get(0);
        }

        return entity;
    }

    private List<SedParticipantStatisticsCountEntity> findAllEntities() {
        Query query = entityManager
                .createQuery("SELECT a from SedParticipantStatisticsCountEntity a where (NOT a.deleted = true)");
        return query.getResultList();
    }

    private Long getUserId() {
        // If there is no user the current context, assume this is a system
        // process
        if (Util.getCurrentUser() == null || Util.getCurrentUser().getId() == null) {
            return MODIFIED_USER_ID;
        } else {
            return Util.getCurrentUser().getId();
        }
    }
}
