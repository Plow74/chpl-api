package gov.healthit.chpl.domain;

import java.io.Serializable;

import gov.healthit.chpl.dto.CertificationResultMacraMeasureDTO;
import gov.healthit.chpl.dto.PendingCertificationResultMacraMeasureDTO;

public class CertificationResultMacraMeasure implements Serializable {
	private static final long serialVersionUID = -8007889129011680045L;
	private Long id;
	Long certificationResultId;
	private MacraMeasure measure;
	
	public CertificationResultMacraMeasure() {
		super();
	}
	
	public CertificationResultMacraMeasure(CertificationResultMacraMeasureDTO dto) {
		this.id = dto.getId();
		this.certificationResultId = dto.getCertificationResultId();
		if(dto.getMeasure() != null) {
			this.measure = new MacraMeasure(dto.getMeasure());
		} 
	}
	
	public CertificationResultMacraMeasure(PendingCertificationResultMacraMeasureDTO dto) {
		this.id = dto.getId();
		this.certificationResultId = dto.getPendingCertificationResultId();
		if(dto.getMacraMeasure() != null) {
			this.measure = new MacraMeasure(dto.getMacraMeasure());
		} else {
			this.measure = new MacraMeasure();
			this.measure.setId(dto.getMacraMeasureId());
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCertificationResultId() {
		return certificationResultId;
	}

	public void setCertificationResultId(Long certificationResultId) {
		this.certificationResultId = certificationResultId;
	}

	public MacraMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(MacraMeasure measure) {
		this.measure = measure;
	}
}
