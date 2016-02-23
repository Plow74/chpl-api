package gov.healthit.chpl.dto;

import java.util.ArrayList;
import java.util.List;

import gov.healthit.chpl.entity.PendingCertificationResultEntity;

public class PendingCertificationResultDTO {
	private Long id;
	private String number;
	private String title;
	private Long certificationCriterionId;
	private Long pendingCertifiedProductId;
	private Boolean meetsCriteria;
	private Boolean gap;
	private Boolean sed;
	private Boolean g1Success;
	private Boolean g2Success;
	private String ucdProcessSelected;
	private String ucdProcessDetails;
	
	private List<PendingCertificationResultAdditionalSoftwareDTO> additionalSoftware;
	private List<PendingCertificationResultTestDataDTO> testData;
	private List<PendingCertificationResultTestFunctionalityDTO> testFunctionality;
	private List<PendingCertificationResultTestProcedureDTO> testProcedures;
	private List<PendingCertificationResultTestStandardDTO> testStandards;
	private List<PendingCertificationResultTestToolDTO> testTools;
	
	public PendingCertificationResultDTO() {
		additionalSoftware = new ArrayList<PendingCertificationResultAdditionalSoftwareDTO>();
		testData = new ArrayList<PendingCertificationResultTestDataDTO>();
		testFunctionality = new ArrayList<PendingCertificationResultTestFunctionalityDTO>();
		testProcedures = new ArrayList<PendingCertificationResultTestProcedureDTO>();
		testStandards = new ArrayList<PendingCertificationResultTestStandardDTO>();
		testTools = new ArrayList<PendingCertificationResultTestToolDTO>();
	}
	
	public PendingCertificationResultDTO(PendingCertificationResultEntity entity) {
		this();
		this.setId(entity.getId());
				
		if(entity.getMappedCriterion() != null) {
			this.setCertificationCriterionId(entity.getMappedCriterion().getId());
			this.setNumber(entity.getMappedCriterion().getNumber());
			this.setTitle(entity.getMappedCriterion().getTitle());
		}
		
		this.setPendingCertifiedProductId(entity.getPendingCertifiedProductId());
		this.setMeetsCriteria(entity.getMeetsCriteria());
		this.setGap(entity.getGap());
		this.setSed(entity.getSed());
		this.setG1Success(entity.getG1Success());
		this.setG2Success(entity.getG2Success());
		this.setUcdProcessDetails(entity.getUcdProcessDetails());
		this.setUcdProcessSelected(entity.getUcdProcessSelected());
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCertificationCriterionId() {
		return certificationCriterionId;
	}
	public void setCertificationCriterionId(Long certificationCriterionId) {
		this.certificationCriterionId = certificationCriterionId;
	}
	public Long getPendingCertifiedProductId() {
		return pendingCertifiedProductId;
	}
	public void setPendingCertifiedProductId(Long pendingCertifiedProductId) {
		this.pendingCertifiedProductId = pendingCertifiedProductId;
	}
	public Boolean getMeetsCriteria() {
		return meetsCriteria;
	}
	public void setMeetsCriteria(Boolean meetsCriteria) {
		this.meetsCriteria = meetsCriteria;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getGap() {
		return gap;
	}

	public void setGap(Boolean gap) {
		this.gap = gap;
	}

	public Boolean getSed() {
		return sed;
	}

	public void setSed(Boolean sed) {
		this.sed = sed;
	}

	public Boolean getG1Success() {
		return g1Success;
	}

	public void setG1Success(Boolean g1Success) {
		this.g1Success = g1Success;
	}

	public Boolean getG2Success() {
		return g2Success;
	}

	public void setG2Success(Boolean g2Success) {
		this.g2Success = g2Success;
	}

	public String getUcdProcessSelected() {
		return ucdProcessSelected;
	}

	public void setUcdProcessSelected(String ucdProcessSelected) {
		this.ucdProcessSelected = ucdProcessSelected;
	}

	public String getUcdProcessDetails() {
		return ucdProcessDetails;
	}

	public void setUcdProcessDetails(String ucdProcessDetails) {
		this.ucdProcessDetails = ucdProcessDetails;
	}

	public List<PendingCertificationResultAdditionalSoftwareDTO> getAdditionalSoftware() {
		return additionalSoftware;
	}

	public void setAdditionalSoftware(List<PendingCertificationResultAdditionalSoftwareDTO> additionalSoftware) {
		this.additionalSoftware = additionalSoftware;
	}

	public List<PendingCertificationResultTestDataDTO> getTestData() {
		return testData;
	}

	public void setTestData(List<PendingCertificationResultTestDataDTO> testData) {
		this.testData = testData;
	}

	public List<PendingCertificationResultTestFunctionalityDTO> getTestFunctionality() {
		return testFunctionality;
	}

	public void setTestFunctionality(List<PendingCertificationResultTestFunctionalityDTO> testFunctionality) {
		this.testFunctionality = testFunctionality;
	}

	public List<PendingCertificationResultTestProcedureDTO> getTestProcedures() {
		return testProcedures;
	}

	public void setTestProcedures(List<PendingCertificationResultTestProcedureDTO> testProcedures) {
		this.testProcedures = testProcedures;
	}

	public List<PendingCertificationResultTestStandardDTO> getTestStandards() {
		return testStandards;
	}

	public void setTestStandards(List<PendingCertificationResultTestStandardDTO> testStandards) {
		this.testStandards = testStandards;
	}

	public List<PendingCertificationResultTestToolDTO> getTestTools() {
		return testTools;
	}

	public void setTestTools(List<PendingCertificationResultTestToolDTO> testTools) {
		this.testTools = testTools;
	}
	
	
}