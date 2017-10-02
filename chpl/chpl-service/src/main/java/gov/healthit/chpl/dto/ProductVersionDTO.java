package gov.healthit.chpl.dto;

import java.io.Serializable;
import java.util.Date;

import gov.healthit.chpl.entity.ProductVersionEntity;

public class ProductVersionDTO implements Serializable {
    private static final long serialVersionUID = -1371133241003414009L;
    private Long id;
    private Date creationDate;
    private Boolean deleted;
    private Date lastModifiedDate;
    private Long lastModifiedUser;
    private Long productId;
    private String productName;
    private String version;

    public ProductVersionDTO() {
    }

    public ProductVersionDTO(ProductVersionEntity entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.creationDate = entity.getCreationDate();
            this.deleted = entity.isDeleted();
            this.lastModifiedDate = entity.getLastModifiedDate();
            this.lastModifiedUser = entity.getLastModifiedUser();
            this.version = entity.getVersion();
            if (entity.getProduct() != null) {
                this.productId = entity.getProduct().getId();
                this.productName = entity.getProduct().getName();
            }
        }
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

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }
}
