package gov.healthit.chpl.domain;

import java.util.ArrayList;
import java.util.List;

public class SurveillanceRequirementOptions {
	private List<KeyValueModel> criteriaOptions2014;
	private List<KeyValueModel> criteriaOptions2015;
	private List<String> transparencyOptions;
	
	public SurveillanceRequirementOptions() {
		criteriaOptions2014 = new ArrayList<KeyValueModel>();
		criteriaOptions2015 = new ArrayList<KeyValueModel>();
		transparencyOptions = new ArrayList<String>();
	}

	public List<String> getTransparencyOptions() {
		return transparencyOptions;
	}

	public void setTransparencyOptions(List<String> transparencyOptions) {
		this.transparencyOptions = transparencyOptions;
	}

	public List<KeyValueModel> getCriteriaOptions2014() {
		return criteriaOptions2014;
	}

	public void setCriteriaOptions2014(List<KeyValueModel> criteriaOptions2014) {
		this.criteriaOptions2014 = criteriaOptions2014;
	}

	public List<KeyValueModel> getCriteriaOptions2015() {
		return criteriaOptions2015;
	}

	public void setCriteriaOptions2015(List<KeyValueModel> criteriaOptions2015) {
		this.criteriaOptions2015 = criteriaOptions2015;
	}
}
