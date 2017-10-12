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

import gov.healthit.chpl.entity.TestStandardEntity;

@Entity
@Table(name = "certification_result_test_standard")
public class CertificationResultTestStandardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "certification_result_test_standard_id")
    private Long id;

    @Basic(optional = false)
    @Column(name = "certification_result_id", nullable = false)
    private Long certificationResultId;

    @Column(name = "test_standard_id")
    private Long testStandardId;

    @Basic(optional = true)
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "test_standard_id", unique = true, nullable = true, insertable = false, updatable = false)
    private TestStandardEntity testStandard;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getCertificationResultId() {
        return certificationResultId;
    }

    public void setCertificationResultId(final Long certificationResultId) {
        this.certificationResultId = certificationResultId;
    }

    public Long getTestStandardId() {
        return testStandardId;
    }

    public void setTestStandardId(final Long testStandardId) {
        this.testStandardId = testStandardId;
    }

    public TestStandardEntity getTestStandard() {
        return testStandard;
    }

    public void setTestStandard(final TestStandardEntity testStandard) {
        this.testStandard = testStandard;
    }

    @Basic(optional = false)
    @Column(name = "creation_date", nullable = false)
    protected Date creationDate;

    @Basic(optional = false)
    @Column(nullable = false)
    protected Boolean deleted;

    @Basic(optional = false)
    @Column(name = "last_modified_date", nullable = false)
    protected Date lastModifiedDate;

    @Basic(optional = false)
    @Column(name = "last_modified_user", nullable = false)
    protected Long lastModifiedUser;

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
