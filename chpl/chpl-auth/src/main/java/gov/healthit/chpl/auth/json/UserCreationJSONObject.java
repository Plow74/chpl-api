package gov.healthit.chpl.auth.json;

public class UserCreationJSONObject {

    private String subjectName;
    private String fullName;
    private String friendlyName;
    private String email;
    private String phoneNumber;
    private String title = null;
    private String password = null;
    private Boolean complianceTermsAccepted = Boolean.FALSE;

    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(final String subjectName) {
        this.subjectName = subjectName;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }
    public String getFriendlyName() {
        return friendlyName;
    }
    public void setFriendlyName(final String friendlyName) {
        this.friendlyName = friendlyName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(final String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(final String title) {
        this.title = title;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(final String password) {
        this.password = password;
    }
    public Boolean getComplianceTermsAccepted() {
        return complianceTermsAccepted;
    }
    public void setComplianceTermsAccepted(final Boolean complianceTermsAccepted) {
        this.complianceTermsAccepted = complianceTermsAccepted;
    }
}
