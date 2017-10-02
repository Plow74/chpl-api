package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.ContactDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.PendingCertifiedProductDAO;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.entity.PendingCertificationResultAdditionalSoftwareEntity;
import gov.healthit.chpl.entity.PendingCertificationResultEntity;
import gov.healthit.chpl.entity.PendingCertificationResultG1MacraMeasureEntity;
import gov.healthit.chpl.entity.PendingCertificationResultG2MacraMeasureEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestDataEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestFunctionalityEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestProcedureEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestStandardEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestTaskEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestTaskParticipantEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestToolEntity;
import gov.healthit.chpl.entity.PendingCertificationResultUcdProcessEntity;
import gov.healthit.chpl.entity.PendingCertifiedProductAccessibilityStandardEntity;
import gov.healthit.chpl.entity.PendingCertifiedProductEntity;
import gov.healthit.chpl.entity.PendingCertifiedProductQmsStandardEntity;
import gov.healthit.chpl.entity.PendingCertifiedProductTargetedUserEntity;
import gov.healthit.chpl.entity.PendingCqmCertificationCriteriaEntity;
import gov.healthit.chpl.entity.PendingCqmCriterionEntity;
import gov.healthit.chpl.entity.PendingTestParticipantEntity;
import gov.healthit.chpl.entity.PendingTestTaskEntity;

@Repository(value="pendingCertifiedProductDAO")
public class PendingCertifiedProductDAOImpl extends BaseDAOImpl implements PendingCertifiedProductDAO {
	private static final Logger logger = LogManager.getLogger(PendingCertifiedProductDAOImpl.class);
	@Autowired MessageSource messageSource;
	@Autowired ContactDAO contactDao;

	@Override
	@Transactional
	public PendingCertifiedProductDTO create(PendingCertifiedProductEntity toCreate) throws EntityCreationException {

			toCreate.setLastModifiedDate(new Date());
			toCreate.setLastModifiedUser(Util.getCurrentUser().getId());
			toCreate.setCreationDate(new Date());
			toCreate.setDeleted(false);
			try {
				entityManager.persist(toCreate);
			} catch(Exception ex) {
				String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.badListingData"), LocaleContextHolder.getLocale()), toCreate.getUniqueId(), ex.getMessage());
				logger.error(msg, ex);
				throw new EntityCreationException(msg);
			}

			for(PendingCertifiedProductQmsStandardEntity qmsStandard : toCreate.getQmsStandards()) {
				qmsStandard.setPendingCertifiedProductId(toCreate.getId());
				qmsStandard.setLastModifiedDate(new Date());
				qmsStandard.setLastModifiedUser(Util.getCurrentUser().getId());
				qmsStandard.setCreationDate(new Date());
				qmsStandard.setDeleted(false);
				try {
					entityManager.persist(qmsStandard);
				} catch(Exception ex) {
					String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.badQmsStandard"), LocaleContextHolder.getLocale()), qmsStandard.getName());
					logger.error(msg, ex);
					throw new EntityCreationException(msg);
				}
			}

			for(PendingCertifiedProductAccessibilityStandardEntity accStandard : toCreate.getAccessibilityStandards()) {
				accStandard.setPendingCertifiedProductId(toCreate.getId());
				accStandard.setLastModifiedDate(new Date());
				accStandard.setLastModifiedUser(Util.getCurrentUser().getId());
				accStandard.setCreationDate(new Date());
				accStandard.setDeleted(false);
				try {
					entityManager.persist(accStandard);
				} catch(Exception ex) {
					String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.badAccessibilityStandard"), LocaleContextHolder.getLocale()), accStandard.getName());
					logger.error(msg, ex);
					throw new EntityCreationException(msg);
				}
			}

			for(PendingCertifiedProductTargetedUserEntity targetedUser : toCreate.getTargetedUsers()) {
				targetedUser.setPendingCertifiedProductId(toCreate.getId());
				targetedUser.setLastModifiedDate(new Date());
				targetedUser.setLastModifiedUser(Util.getCurrentUser().getId());
				targetedUser.setCreationDate(new Date());
				targetedUser.setDeleted(false);
				try {
					entityManager.persist(targetedUser);
				} catch(Exception ex) {
					String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.badTargetedUser"), LocaleContextHolder.getLocale()), targetedUser.getName());
					logger.error(msg, ex);
					throw new EntityCreationException(msg);
				}
			}

			for(PendingCertificationResultEntity criterion : toCreate.getCertificationCriterion()) {
				criterion.setPendingCertifiedProductId(toCreate.getId());
				criterion.setLastModifiedDate(new Date());
				criterion.setLastModifiedUser(Util.getCurrentUser().getId());
				criterion.setCreationDate(new Date());
				criterion.setDeleted(false);
				try {
					entityManager.persist(criterion);
				} catch(Exception ex) {
					String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.badCriteriaData"), LocaleContextHolder.getLocale()), criterion.getMappedCriterion().getNumber(), ex.getMessage());
					logger.error(msg, ex);
					throw new EntityCreationException(msg);
				}

				if(criterion.getUcdProcesses() != null && criterion.getUcdProcesses().size() > 0) {
					for(PendingCertificationResultUcdProcessEntity ucd : criterion.getUcdProcesses()) {
						ucd.setPendingCertificationResultId(criterion.getId());
						ucd.setLastModifiedDate(new Date());
						ucd.setLastModifiedUser(Util.getCurrentUser().getId());
						ucd.setCreationDate(new Date());
						ucd.setDeleted(false);
						try {
							entityManager.persist(ucd);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badUcdProcess"), LocaleContextHolder.getLocale()),
									ucd.getUcdProcessName());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getTestStandards() != null && criterion.getTestStandards().size() > 0) {
					for(PendingCertificationResultTestStandardEntity tsEntity : criterion.getTestStandards()) {
						tsEntity.setPendingCertificationResultId(criterion.getId());
						tsEntity.setLastModifiedDate(new Date());
						tsEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						tsEntity.setCreationDate(new Date());
						tsEntity.setDeleted(false);
						try {
							entityManager.persist(tsEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestStandard"), LocaleContextHolder.getLocale()),
									tsEntity.getTestStandardName());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}
				if(criterion.getTestFunctionality() != null && criterion.getTestFunctionality().size() > 0) {
					for(PendingCertificationResultTestFunctionalityEntity tfEntity : criterion.getTestFunctionality()) {
						tfEntity.setPendingCertificationResultId(criterion.getId());
						tfEntity.setLastModifiedDate(new Date());
						tfEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						tfEntity.setCreationDate(new Date());
						tfEntity.setDeleted(false);
						try {
							entityManager.persist(tfEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestFunctionality"), LocaleContextHolder.getLocale()),
									tfEntity.getTestFunctionalityNumber());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getAdditionalSoftware() != null && criterion.getAdditionalSoftware().size() > 0) {
					for(PendingCertificationResultAdditionalSoftwareEntity asEntity : criterion.getAdditionalSoftware()) {
						asEntity.setPendingCertificationResultId(criterion.getId());
						asEntity.setLastModifiedDate(new Date());
						asEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						asEntity.setCreationDate(new Date());
						asEntity.setDeleted(false);
						try {
							entityManager.persist(asEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badAdditionalSoftware"), LocaleContextHolder.getLocale()),
										(StringUtils.isEmpty(asEntity.getSoftwareName()) ? asEntity.getChplId() : asEntity.getSoftwareName()));
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getTestProcedures() != null && criterion.getTestProcedures().size() > 0) {
					for(PendingCertificationResultTestProcedureEntity tpEntity : criterion.getTestProcedures()) {
						tpEntity.setPendingCertificationResultId(criterion.getId());
						tpEntity.setLastModifiedDate(new Date());
						tpEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						tpEntity.setCreationDate(new Date());
						tpEntity.setDeleted(false);
						try {
							entityManager.persist(tpEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestProcedure"), LocaleContextHolder.getLocale()),
									tpEntity.getTestProcedureVersion());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getTestData() != null && criterion.getTestData().size() > 0) {
					for(PendingCertificationResultTestDataEntity tdEntity : criterion.getTestData()) {
						tdEntity.setPendingCertificationResultId(criterion.getId());
						tdEntity.setLastModifiedDate(new Date());
						tdEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						tdEntity.setCreationDate(new Date());
						tdEntity.setDeleted(false);
						try {
							entityManager.persist(tdEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestData"), LocaleContextHolder.getLocale()),
									tdEntity.getVersion());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getTestTools() != null && criterion.getTestTools().size() > 0) {
					for(PendingCertificationResultTestToolEntity ttEntity : criterion.getTestTools()) {
						ttEntity.setPendingCertificationResultId(criterion.getId());
						ttEntity.setLastModifiedDate(new Date());
						ttEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						ttEntity.setCreationDate(new Date());
						ttEntity.setDeleted(false);
						try {
							entityManager.persist(ttEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestTool"), LocaleContextHolder.getLocale()),
									ttEntity.getTestToolName());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getG1MacraMeasures() != null && criterion.getG1MacraMeasures().size() > 0) {
					for(PendingCertificationResultG1MacraMeasureEntity mmEntity : criterion.getG1MacraMeasures()) {
						mmEntity.setPendingCertificationResultId(criterion.getId());
						mmEntity.setLastModifiedDate(new Date());
						mmEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						mmEntity.setCreationDate(new Date());
						mmEntity.setDeleted(false);
						try {
							entityManager.persist(mmEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badG1MacraMeasure"), LocaleContextHolder.getLocale()),
									mmEntity.getEnteredValue());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getG2MacraMeasures() != null && criterion.getG2MacraMeasures().size() > 0) {
					for(PendingCertificationResultG2MacraMeasureEntity mmEntity : criterion.getG2MacraMeasures()) {
						mmEntity.setPendingCertificationResultId(criterion.getId());
						mmEntity.setLastModifiedDate(new Date());
						mmEntity.setLastModifiedUser(Util.getCurrentUser().getId());
						mmEntity.setCreationDate(new Date());
						mmEntity.setDeleted(false);
						try {
							entityManager.persist(mmEntity);
						} catch(Exception ex) {
							String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badG2MacraMeasure"), LocaleContextHolder.getLocale()),
									mmEntity.getEnteredValue());
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}

				if(criterion.getTestTasks() != null && criterion.getTestTasks().size() > 0) {
					for(PendingCertificationResultTestTaskEntity ttEntity : criterion.getTestTasks()) {
						if(ttEntity.getTestTask() != null) {
							PendingTestTaskEntity testTask = ttEntity.getTestTask();
							if(testTask.getId() == null) {
								testTask.setLastModifiedDate(new Date());
								testTask.setLastModifiedUser(Util.getCurrentUser().getId());
								testTask.setCreationDate(new Date());
								testTask.setDeleted(false);
								try {
									entityManager.persist(testTask);
								} catch(Exception ex) {
									String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestTask"), LocaleContextHolder.getLocale()),
											testTask.getUniqueId());
									logger.error(msg, ex);
									throw new EntityCreationException(msg);
								}
							}
							ttEntity.setPendingTestTaskId(testTask.getId());
							ttEntity.setPendingCertificationResultId(criterion.getId());
							ttEntity.setLastModifiedDate(new Date());
							ttEntity.setLastModifiedUser(Util.getCurrentUser().getId());
							ttEntity.setCreationDate(new Date());
							ttEntity.setDeleted(false);
							entityManager.persist(ttEntity);
						}

						if(ttEntity.getTestParticipants() != null && ttEntity.getTestParticipants().size() > 0) {
							for(PendingCertificationResultTestTaskParticipantEntity ttPartEntity : ttEntity.getTestParticipants()) {
								if(ttPartEntity.getTestParticipant() != null) {
									PendingTestParticipantEntity partEntity = ttPartEntity.getTestParticipant();
									if(partEntity.getId() == null) {
										partEntity.setLastModifiedDate(new Date());
										partEntity.setLastModifiedUser(Util.getCurrentUser().getId());
										partEntity.setCreationDate(new Date());
										partEntity.setDeleted(false);
										try {
											entityManager.persist(partEntity);
										} catch(Exception ex) {
											String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("listing.criteria.badTestParticipant"), LocaleContextHolder.getLocale()),
													partEntity.getUniqueId());
											logger.error(msg, ex);
											throw new EntityCreationException(msg);
										}
									}
									ttPartEntity.setPendingTestParticipantId(partEntity.getId());
									ttPartEntity.setPendingCertificationResultTestTaskId(ttEntity.getId());
									ttPartEntity.setLastModifiedDate(new Date());
									ttPartEntity.setLastModifiedUser(Util.getCurrentUser().getId());
									ttPartEntity.setCreationDate(new Date());
									ttPartEntity.setDeleted(false);
									entityManager.persist(ttPartEntity);
								}
							}
						}
					}
				}
			}

			for(PendingCqmCriterionEntity cqm : toCreate.getCqmCriterion()) {
				cqm.setPendingCertifiedProductId(toCreate.getId());
				if(cqm.getLastModifiedDate() == null) {
					cqm.setLastModifiedDate(new Date());
				}
				if(cqm.getLastModifiedUser() == null) {
					cqm.setLastModifiedUser(Util.getCurrentUser().getId());
				}
				cqm.setCreationDate(new Date());
				cqm.setDeleted(false);
				try {
					entityManager.persist(cqm);
				} catch(Exception ex) {
					String msg = "Could not process CQM '" + cqm.getMappedCriterion().getTitle() + "'. ";
					logger.error(msg, ex);
					throw new EntityCreationException(msg);
				}

				if(cqm.getCertifications() != null && cqm.getCertifications().size() > 0) {
					for(PendingCqmCertificationCriteriaEntity cert : cqm.getCertifications()) {
						cert.setPendingCqmId(cqm.getId());
						cert.setDeleted(false);
						cert.setLastModifiedUser(Util.getCurrentUser().getId());
						cert.setCreationDate(new Date());
						cert.setLastModifiedDate(new Date());
						try {
							entityManager.persist(cert);
						} catch(Exception ex) {
							String msg = "Could not process CQM Criteria '" + cert.getCertificationCriteria().getNumber() + "'. ";
							logger.error(msg, ex);
							throw new EntityCreationException(msg);
						}
					}
				}
			}

		return new PendingCertifiedProductDTO(toCreate);
	}

	@Override
	@Transactional
	public void delete(Long pendingProductId) throws EntityRetrievalException {
		PendingCertifiedProductEntity entity;
		entity = getEntityById(pendingProductId, true);
		entity.setDeleted(true);
		entity.setLastModifiedDate(new Date());
		entity.setLastModifiedUser(Util.getCurrentUser().getId());
		entityManager.persist(entity);
	}

	public List<PendingCertifiedProductDTO> findAll() {
		List<PendingCertifiedProductEntity> entities = getAllEntities();
		List<PendingCertifiedProductDTO> dtos = new ArrayList<>();

		for (PendingCertifiedProductEntity entity : entities) {
			PendingCertifiedProductDTO dto = new PendingCertifiedProductDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	@Transactional
	public List<PendingCertifiedProductDTO> findByStatus(Long statusId) {
		List<PendingCertifiedProductEntity> entities = getEntitiesByStatus(statusId);
		List<PendingCertifiedProductDTO> dtos = new ArrayList<>();

		for (PendingCertifiedProductEntity entity : entities) {
			PendingCertifiedProductDTO dto = new PendingCertifiedProductDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	public PendingCertifiedProductDTO findById(Long pcpId, boolean includeDeleted) throws EntityRetrievalException {
		PendingCertifiedProductEntity entity = getEntityById(pcpId, includeDeleted);
		if(entity == null) {
			return null;
		}
		return new PendingCertifiedProductDTO(entity);
	}

	@Cacheable("findByAcbId")
	public List<PendingCertifiedProductDTO> findByAcbId(Long acbId) {
		List<PendingCertifiedProductEntity> entities = getEntityByAcbId(acbId);
		List<PendingCertifiedProductDTO> dtos = new ArrayList<>();

		for (PendingCertifiedProductEntity entity : entities) {
			PendingCertifiedProductDTO dto = new PendingCertifiedProductDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	public Long findIdByOncId(String id) throws EntityRetrievalException {
		PendingCertifiedProductEntity entity = getEntityByOncId(id);
		if(entity == null) {
			return null;
		}
		return entity.getId();
	}

	private void update(PendingCertifiedProductEntity product) {

		entityManager.merge(product);

	}

	private List<PendingCertifiedProductEntity> getAllEntities() {

		List<PendingCertifiedProductEntity> result = entityManager.createQuery(
				"SELECT pcp from PendingCertifiedProductEntity pcp "
				+ "WHERE (not pcp.deleted = true)", PendingCertifiedProductEntity.class).getResultList();
		return result;

	}

	private PendingCertifiedProductEntity getEntityById(Long entityId, boolean includeDeleted) throws EntityRetrievalException {
		PendingCertifiedProductEntity entity = null;
		String hql = "SELECT DISTINCT pcp from PendingCertifiedProductEntity pcp "
				+ " where pcp.id = :entityid";
		if(!includeDeleted){
			hql += " and pcp.deleted <> true";
		}

		Query query = entityManager.createQuery(hql, PendingCertifiedProductEntity.class);
		query.setParameter("entityid", entityId);
		List<PendingCertifiedProductEntity> result = query.getResultList();

		if(result == null || result.size() == 0) {
			String msg = String.format(messageSource.getMessage(new DefaultMessageSourceResolvable("pendingListing.notFound"), LocaleContextHolder.getLocale()));
			throw new EntityRetrievalException(msg);
		} else if(result.size() > 0) {
			entity = result.get(0);
		}

		return entity;
	}

	private PendingCertifiedProductEntity getEntityByOncId(String id) throws EntityRetrievalException {

		PendingCertifiedProductEntity entity = null;

		Query query = entityManager.createQuery( "SELECT pcp from PendingCertifiedProductEntity pcp "
				+ " where (unique_id = :id) "
				+ " and (not pcp.deleted = true)", PendingCertifiedProductEntity.class );
		query.setParameter("id", id);
		List<PendingCertifiedProductEntity> result = query.getResultList();

		if (result.size() > 1){
			throw new EntityRetrievalException("Data error. Duplicate ONC id in database.");
		}

		if (result.size() > 0){
			entity = result.get(0);
		}

		return entity;
	}

	private List<PendingCertifiedProductEntity> getEntityByAcbId(Long acbId) {

		Query query = entityManager.createQuery( "SELECT pcp from PendingCertifiedProductEntity pcp "
				+ " where (certification_body_id = :acbId) "
				+ " and not (pcp.deleted = true)", PendingCertifiedProductEntity.class );
		query.setParameter("acbId", acbId);
		List<PendingCertifiedProductEntity> result = query.getResultList();
		return result;
	}

	private List<PendingCertifiedProductEntity> getEntitiesByStatus(Long statusId) {

		Query query = entityManager.createQuery( "SELECT pcp from PendingCertifiedProductEntity pcp "
				+ " where (certification_status_id = :statusId) "
				+ " and not (pcp.deleted = true)", PendingCertifiedProductEntity.class );
		query.setParameter("statusId", statusId);
		List<PendingCertifiedProductEntity> result = query.getResultList();
		return result;
	}
}
