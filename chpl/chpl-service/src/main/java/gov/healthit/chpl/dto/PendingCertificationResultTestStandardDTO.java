package gov.healthit.chpl.dto;

import java.io.Serializable;

import gov.healthit.chpl.entity.PendingCertificationResultTestStandardEntity;

public class PendingCertificationResultTestStandardDTO implements Serializable {
	private static final long serialVersionUID = -4957314047711686694L;
	private Long id;
	private Long pendingCertificationResultId;
	private Long testStandardId;
	private String name;

	public PendingCertificationResultTestStandardDTO() {}

	public PendingCertificationResultTestStandardDTO(PendingCertificationResultTestStandardEntity entity) {
		this.setId(entity.getId());
		this.setPendingCertificationResultId(entity.getPendingCertificationResultId());
		this.setTestStandardId(entity.getTestStandardId());
		this.setName(entity.getTestStandardName());
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getPendingCertificationResultId() {
		return pendingCertificationResultId;
	}

	public void setPendingCertificationResultId(Long pendingCertificationResultId) {
		this.pendingCertificationResultId = pendingCertificationResultId;
	}

	public Long getTestStandardId() {
		return testStandardId;
	}

	public void setTestStandardId(Long testStandardId) {
		this.testStandardId = testStandardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
