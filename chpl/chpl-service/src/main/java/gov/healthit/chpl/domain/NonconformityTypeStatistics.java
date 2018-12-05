package gov.healthit.chpl.domain;

import java.util.Date;

import gov.healthit.chpl.dto.NonconformityTypeStatisticsDTO;

public class NonconformityTypeStatistics {

    private Long id;
    private Long nonconformityCount;
    private String nonconformityType;
    private Boolean deleted;
    private Long lastModifiedUser;
    private Date creationDate;
    private Date lastModifiedDate;
    
    public NonconformityTypeStatistics(NonconformityTypeStatisticsDTO dto){
    	this.nonconformityCount = dto.getNonconformityCount();
    	this.nonconformityType = dto.getNonconformityType();
    	this.setCreationDate(dto.getCreationDate());
    	this.setDeleted(dto.getDeleted());
    	this.setLastModifiedUser(dto.getLastModifiedUser());
    	this.setLastModifiedDate(dto.getLastModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getNonconformityCount() {
		return nonconformityCount;
	}

	public void setNonconformityCount(Long nonconformityCount) {
		this.nonconformityCount = nonconformityCount;
	}

	public String getNonconformityType() {
		return nonconformityType;
	}

	public void setNonconformityType(String nonconformityType) {
		this.nonconformityType = nonconformityType;
	}

	public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getLastModifiedUser() {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(final Long lastModifiedUser) {
        this.lastModifiedUser = lastModifiedUser;
    }
}
