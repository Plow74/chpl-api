package gov.healthit.chpl.entity.surveillance;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import gov.healthit.chpl.entity.CertificationCriterionEntity;


@Entity
@Table(name = "surveillance_requirement")
public class SurveillanceRequirementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "surveillance_id")
	private Long surveillanceId;

	@Column(name = "type_id")
	private Long surveillanceRequirementTypeId;

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id", insertable = false, updatable = false)
	private SurveillanceRequirementTypeEntity surveillanceRequirementType;

	@Column(name = "certification_criterion_id")
	private Long certificationCriterionId;

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "certification_criterion_id", insertable = false, updatable = false)
	private CertificationCriterionEntity certificationCriterionEntity;

	@Column(name = "requirement")
	private String surveilledRequirement;

	@Column(name = "result_id")
	private Long surveillanceResultTypeId;

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "result_id", insertable = false, updatable = false)
	private SurveillanceResultTypeEntity surveillanceResultTypeEntity;

	@Column( name = "deleted")
	private Boolean deleted;

	@Column( name = "last_modified_user")
	private Long lastModifiedUser;

	@Column( name = "creation_date", insertable = false, updatable = false  )
	private Date creationDate;

	@Column( name = "last_modified_date", insertable = false, updatable = false )
	private Date lastModifiedDate;

	@OneToMany( fetch = FetchType.LAZY, mappedBy = "surveillanceRequirementId"  )
	@Basic( optional = false )
	@Column( name = "surveillance_requirement_id", nullable = false  )
	@Where(clause="deleted <> 'true'")
	private Set<SurveillanceNonconformityEntity> nonconformities = new HashSet<SurveillanceNonconformityEntity>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSurveilledRequirement() {
		return surveilledRequirement;
	}

	public void setSurveilledRequirement(String surveilledRequirement) {
		this.surveilledRequirement = surveilledRequirement;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Long getLastModifiedUser() {
		return lastModifiedUser;
	}
	public void setLastModifiedUser(Long lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public Long getSurveillanceId() {
		return surveillanceId;
	}

	public void setSurveillanceId(Long surveillanceId) {
		this.surveillanceId = surveillanceId;
	}

	public Long getSurveillanceRequirementTypeId() {
		return surveillanceRequirementTypeId;
	}

	public void setSurveillanceRequirementTypeId(Long surveillanceRequirementTypeId) {
		this.surveillanceRequirementTypeId = surveillanceRequirementTypeId;
	}

	public SurveillanceRequirementTypeEntity getSurveillanceRequirementType() {
		return surveillanceRequirementType;
	}

	public void setSurveillanceRequirementType(SurveillanceRequirementTypeEntity surveillanceRequirementType) {
		this.surveillanceRequirementType = surveillanceRequirementType;
	}

	public Long getCertificationCriterionId() {
		return certificationCriterionId;
	}

	public void setCertificationCriterionId(Long certificationCriterionId) {
		this.certificationCriterionId = certificationCriterionId;
	}

	public CertificationCriterionEntity getCertificationCriterionEntity() {
		return certificationCriterionEntity;
	}

	public void setCertificationCriterionEntity(CertificationCriterionEntity certificationCriterionEntity) {
		this.certificationCriterionEntity = certificationCriterionEntity;
	}

	public Long getSurveillanceResultTypeId() {
		return surveillanceResultTypeId;
	}

	public void setSurveillanceResultTypeId(Long surveillanceResultTypeId) {
		this.surveillanceResultTypeId = surveillanceResultTypeId;
	}

	public SurveillanceResultTypeEntity getSurveillanceResultTypeEntity() {
		return surveillanceResultTypeEntity;
	}

	public void setSurveillanceResultTypeEntity(SurveillanceResultTypeEntity surveillanceResultTypeEntity) {
		this.surveillanceResultTypeEntity = surveillanceResultTypeEntity;
	}

	public Set<SurveillanceNonconformityEntity> getNonconformities() {
		return nonconformities;
	}

	public void setNonconformities(Set<SurveillanceNonconformityEntity> nonconformities) {
		this.nonconformities = nonconformities;
	}
}
