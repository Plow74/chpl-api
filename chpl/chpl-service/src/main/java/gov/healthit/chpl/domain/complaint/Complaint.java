package gov.healthit.chpl.domain.complaint;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import gov.healthit.chpl.domain.CertificationBody;
import gov.healthit.chpl.dto.ComplaintCriterionMapDTO;
import gov.healthit.chpl.dto.ComplaintDTO;
import gov.healthit.chpl.dto.ComplaintListingMapDTO;

public class Complaint {
    private Long id;
    private CertificationBody certificationBody;
    private ComplaintType complaintType;
    private ComplaintStatusType complaintStatusType;
    private String oncComplaintId;
    private String acbComplaintId;
    private Date receivedDate;
    private String summary;
    private String actions;
    private boolean complainantContacted;
    private boolean developerContacted;
    private boolean oncAtlContacted;
    private boolean flagForOncReview;
    private Date closedDate;
    private Set<ComplaintListingMap> listings = new HashSet<ComplaintListingMap>();
    private Set<ComplaintCriterionMap> criteria = new HashSet<ComplaintCriterionMap>();

    public Complaint() {

    }

    public Complaint(ComplaintDTO dto) {
        BeanUtils.copyProperties(dto, this);

        listings = new HashSet<ComplaintListingMap>();
        for (ComplaintListingMapDTO clDTO : dto.getListings()) {
            listings.add(new ComplaintListingMap(clDTO));
        }

        criteria = new HashSet<ComplaintCriterionMap>();
        for (ComplaintCriterionMapDTO criterionDTO : dto.getCriteria()) {
            criteria.add(new ComplaintCriterionMap(criterionDTO));
        }

        this.certificationBody = new CertificationBody(dto.getCertificationBody());
        this.complaintStatusType = new ComplaintStatusType(dto.getComplaintStatusType());
        this.complaintType = new ComplaintType(dto.getComplaintType());
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public CertificationBody getCertificationBody() {
        return certificationBody;
    }

    public void setCertificationBody(final CertificationBody certificationBody) {
        this.certificationBody = certificationBody;
    }

    public ComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(final ComplaintType complaintType) {
        this.complaintType = complaintType;
    }

    public ComplaintStatusType getComplaintStatusType() {
        return complaintStatusType;
    }

    public void setComplaintStatusType(final ComplaintStatusType complaintStatusType) {
        this.complaintStatusType = complaintStatusType;
    }

    public String getOncComplaintId() {
        return oncComplaintId;
    }

    public void setOncComplaintId(final String oncComplaintId) {
        this.oncComplaintId = oncComplaintId;
    }

    public String getAcbComplaintId() {
        return acbComplaintId;
    }

    public void setAcbComplaintId(final String acbComplaintId) {
        this.acbComplaintId = acbComplaintId;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(final Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(final String actions) {
        this.actions = actions;
    }

    public boolean isComplainantContacted() {
        return complainantContacted;
    }

    public void setComplainantContacted(final boolean complainantContacted) {
        this.complainantContacted = complainantContacted;
    }

    public boolean isDeveloperContacted() {
        return developerContacted;
    }

    public void setDeveloperContacted(final boolean developerContacted) {
        this.developerContacted = developerContacted;
    }

    public boolean isOncAtlContacted() {
        return oncAtlContacted;
    }

    public void setOncAtlContacted(final boolean oncAtlContacted) {
        this.oncAtlContacted = oncAtlContacted;
    }

    public boolean isFlagForOncReview() {
        return flagForOncReview;
    }

    public void setFlagForOncReview(final boolean flagForOncReview) {
        this.flagForOncReview = flagForOncReview;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(final Date closedDate) {
        this.closedDate = closedDate;
    }

    public Set<ComplaintListingMap> getListings() {
        return listings;
    }

    public void setListings(final Set<ComplaintListingMap> listings) {
        this.listings = listings;
    }

    public Set<ComplaintCriterionMap> getCriteria() {
        return criteria;
    }

    public void setCriteria(Set<ComplaintCriterionMap> criteria) {
        this.criteria = criteria;
    }

}
