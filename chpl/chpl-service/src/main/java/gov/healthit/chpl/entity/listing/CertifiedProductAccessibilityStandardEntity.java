package gov.healthit.chpl.entity.listing;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import gov.healthit.chpl.entity.AccessibilityStandardEntity;


@Entity
@Table(name = "certified_product_accessibility_standard")
public class CertifiedProductAccessibilityStandardEntity {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic( optional = false )
	@Column(name = "certified_product_accessibility_standard_id")
	private Long id;
	
	@Basic( optional = false )
	@Column( name = "certified_product_id", nullable = false  )
	private Long certifiedProductId;

	@Basic( optional = false )
	@Column( name = "accessibility_standard_id", nullable = false  )
	private Long accessibilityStandardId;
	
	@Basic( optional = true )
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "accessibility_standard_id", unique = true, nullable = true, insertable = false, updatable = false)
	private AccessibilityStandardEntity accessibilityStandard;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCertifiedProductId() {
		return certifiedProductId;
	}

	public void setCertifiedProductId(Long certifiedProductId) {
		this.certifiedProductId = certifiedProductId;
	}


	@Basic( optional = false )
	@Column( name = "creation_date", nullable = false  )
	protected Date creationDate;
	
	@Basic( optional = false )
	@Column( name = "deleted", nullable = false  )
	protected Boolean deleted;
	
	@Basic( optional = false )
	@Column( name = "last_modified_date", nullable = false  )
	protected Date lastModifiedDate;
	
	@Basic( optional = false )
	@Column( name = "last_modified_user", nullable = false  )
	protected Long lastModifiedUser;
	
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

	public Long getAccessibilityStandardId() {
		return accessibilityStandardId;
	}

	public void setAccessibilityStandardId(Long accessibilityStandardId) {
		this.accessibilityStandardId = accessibilityStandardId;
	}

	public AccessibilityStandardEntity getAccessibilityStandard() {
		return accessibilityStandard;
	}

	public void setAccessibilityStandard(AccessibilityStandardEntity accessibilityStandard) {
		this.accessibilityStandard = accessibilityStandard;
	}
}
