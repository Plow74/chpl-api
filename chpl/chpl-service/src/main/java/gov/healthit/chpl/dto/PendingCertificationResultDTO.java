package gov.healthit.chpl.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gov.healthit.chpl.entity.PendingCertificationResultAdditionalSoftwareEntity;
import gov.healthit.chpl.entity.PendingCertificationResultEntity;
import gov.healthit.chpl.entity.PendingCertificationResultG1MacraMeasureEntity;
import gov.healthit.chpl.entity.PendingCertificationResultG2MacraMeasureEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestDataEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestFunctionalityEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestProcedureEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestStandardEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestTaskEntity;
import gov.healthit.chpl.entity.PendingCertificationResultTestToolEntity;
import gov.healthit.chpl.entity.PendingCertificationResultUcdProcessEntity;

public class PendingCertificationResultDTO implements Serializable {
	private static final long serialVersionUID = -1026669045107851464L;
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
	private String apiDocumentation;
	private String privacySecurityFramework;
	
	private List<PendingCertificationResultUcdProcessDTO> ucdProcesses;
	private List<PendingCertificationResultAdditionalSoftwareDTO> additionalSoftware;
	private List<PendingCertificationResultTestDataDTO> testData;
	private List<PendingCertificationResultTestFunctionalityDTO> testFunctionality;
	private List<PendingCertificationResultTestProcedureDTO> testProcedures;
	private List<PendingCertificationResultTestStandardDTO> testStandards;
	private List<PendingCertificationResultTestToolDTO> testTools;
	private List<PendingCertificationResultMacraMeasureDTO> g1MacraMeasures;
	private List<PendingCertificationResultMacraMeasureDTO> g2MacraMeasures;
	private List<PendingCertificationResultTestTaskDTO> testTasks;
	
	public PendingCertificationResultDTO() {
		ucdProcesses = new ArrayList<PendingCertificationResultUcdProcessDTO>();
		additionalSoftware = new ArrayList<PendingCertificationResultAdditionalSoftwareDTO>();
		testData = new ArrayList<PendingCertificationResultTestDataDTO>();
		testFunctionality = new ArrayList<PendingCertificationResultTestFunctionalityDTO>();
		testProcedures = new ArrayList<PendingCertificationResultTestProcedureDTO>();
		testStandards = new ArrayList<PendingCertificationResultTestStandardDTO>();
		testTools = new ArrayList<PendingCertificationResultTestToolDTO>();
		g1MacraMeasures = new ArrayList<PendingCertificationResultMacraMeasureDTO>();
		g2MacraMeasures = new ArrayList<PendingCertificationResultMacraMeasureDTO>();
		testTasks = new ArrayList<PendingCertificationResultTestTaskDTO>();
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
		this.apiDocumentation = entity.getApiDocumentation();
		this.privacySecurityFramework = entity.getPrivacySecurityFramework();
		
		if(entity.getUcdProcesses() != null && entity.getUcdProcesses().size() > 0) {
			for(PendingCertificationResultUcdProcessEntity e : entity.getUcdProcesses()) {
				this.getUcdProcesses().add(new PendingCertificationResultUcdProcessDTO(e));
			}
			this.setSed(Boolean.TRUE);
		}
		
		if(entity.getTestStandards() != null) {
			for(PendingCertificationResultTestStandardEntity e : entity.getTestStandards()) {
				this.getTestStandards().add(new PendingCertificationResultTestStandardDTO(e));
			}
		}
		if(entity.getTestFunctionality() != null) {
			for(PendingCertificationResultTestFunctionalityEntity e : entity.getTestFunctionality()) {
				this.getTestFunctionality().add(new PendingCertificationResultTestFunctionalityDTO(e));
			}
		}
		if(entity.getAdditionalSoftware() != null) {
			for(PendingCertificationResultAdditionalSoftwareEntity e : entity.getAdditionalSoftware()) {
				this.getAdditionalSoftware().add(new PendingCertificationResultAdditionalSoftwareDTO(e));
			}
		}
		if(entity.getTestProcedures() != null) {
			for(PendingCertificationResultTestProcedureEntity e : entity.getTestProcedures()) {
				this.getTestProcedures().add(new PendingCertificationResultTestProcedureDTO(e));
			}
		}
		if(entity.getTestData() != null) {
			for(PendingCertificationResultTestDataEntity e : entity.getTestData()) {
				this.getTestData().add(new PendingCertificationResultTestDataDTO(e));
			}
		}
		if(entity.getTestTools() != null) {
			for(PendingCertificationResultTestToolEntity e : entity.getTestTools()) {
				this.getTestTools().add(new PendingCertificationResultTestToolDTO(e));
			}
		}
		
		if(entity.getG1MacraMeasures() != null) {
			for(PendingCertificationResultG1MacraMeasureEntity e : entity.getG1MacraMeasures()) {
				this.getG1MacraMeasures().add(new PendingCertificationResultMacraMeasureDTO(e));
			}
		}
		
		if(entity.getG2MacraMeasures() != null) {
			for(PendingCertificationResultG2MacraMeasureEntity e : entity.getG2MacraMeasures()) {
				this.getG2MacraMeasures().add(new PendingCertificationResultMacraMeasureDTO(e));
			}
		}
		
		if(entity.getTestTasks() != null && entity.getTestTasks().size() > 0) {
			for(PendingCertificationResultTestTaskEntity e : entity.getTestTasks()) {
				PendingCertificationResultTestTaskDTO taskDto = new PendingCertificationResultTestTaskDTO(e);
				this.getTestTasks().add(taskDto);
			}
			this.setSed(Boolean.TRUE);
		}
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

	public List<PendingCertificationResultUcdProcessDTO> getUcdProcesses() {
		return ucdProcesses;
	}

	public void setUcdProcesses(List<PendingCertificationResultUcdProcessDTO> ucdProcesses) {
		this.ucdProcesses = ucdProcesses;
	}

	public List<PendingCertificationResultTestTaskDTO> getTestTasks() {
		return testTasks;
	}

	public void setTestTasks(List<PendingCertificationResultTestTaskDTO> testTasks) {
		this.testTasks = testTasks;
	}

	public String getApiDocumentation() {
		return apiDocumentation;
	}

	public void setApiDocumentation(String apiDocumentation) {
		this.apiDocumentation = apiDocumentation;
	}

	public String getPrivacySecurityFramework() {
		return privacySecurityFramework;
	}

	public void setPrivacySecurityFramework(String privacySecurityFramework) {
		this.privacySecurityFramework = privacySecurityFramework;
	}

	public List<PendingCertificationResultMacraMeasureDTO> getG1MacraMeasures() {
		return g1MacraMeasures;
	}

	public void setG1MacraMeasures(List<PendingCertificationResultMacraMeasureDTO> g1Measures) {
		this.g1MacraMeasures = g1Measures;
	}

	public List<PendingCertificationResultMacraMeasureDTO> getG2MacraMeasures() {
		return g2MacraMeasures;
	}

	public void setG2MacraMeasures(List<PendingCertificationResultMacraMeasureDTO> g2Measures) {
		this.g2MacraMeasures = g2Measures;
	}
	
	
}
