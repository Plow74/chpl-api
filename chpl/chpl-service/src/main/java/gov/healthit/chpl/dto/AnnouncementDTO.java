package gov.healthit.chpl.dto;

import java.io.Serializable;
import java.util.Date;

import gov.healthit.chpl.entity.AnnouncementEntity;

public class AnnouncementDTO implements Serializable {
    private static final long serialVersionUID = -964637928651441411L;
    private Long id;
    private String title;
    private String text;
    private Date startDate;
    private Date endDate;
    private Boolean isPublic;
    private Date creationDate;
    private Boolean deleted;
    private Date lastModifiedDate;
    private Long lastModifiedUser;

    public AnnouncementDTO() {
    }

    public AnnouncementDTO(AnnouncementEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.text = entity.getText();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.isPublic = entity.getIsPublic();
        this.deleted = entity.getDeleted();
        this.lastModifiedDate = entity.getLastModifiedDate();
        this.lastModifiedUser = entity.getLastModifiedUser();
        this.creationDate = entity.getCreationDate();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(final Boolean isPublic) {
        this.isPublic = isPublic;
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
