package gov.healthit.chpl.auth.user;
import gov.healthit.chpl.auth.permission.UserPermission;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDTO implements UserDetails {

	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String subjectName;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String title;
	private Set<UserPermission> permissions = new HashSet<UserPermission>();
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
	private boolean accountEnabled;
	
	
	public UserDTO(){}
	
	public UserDTO(UserEntity entity){
		
		this.id = entity.getId();
		this.subjectName = entity.getSubjectName();
		this.firstName = entity.getFirstName();
		this.lastName = entity.getLastName();
		this.email = entity.getContact().getEmail();
		this.phoneNumber = entity.getContact().getPhoneNumber();
		this.title = entity.getContact().getEmail();
		this.permissions = entity.getPermissions();
		this.accountExpired = !entity.isAccountNonExpired();
		this.accountLocked = !entity.isAccountNonLocked();
		this.accountEnabled = entity.isEnabled();
		
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSubjectName() {
		return subjectName;
	}
	
	public void setSubjectName(String subject) {
		this.subjectName = subject;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Set<UserPermission> getPermissions() {
		return this.permissions;
	}
	
	public void addPermission(UserPermission permission){
		this.permissions.add(permission);
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getPermissions();
	}
	
	public String getName() {
		return subjectName;
	}
	
	@Override
	public String getUsername() {
		return subjectName;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}
	
	@Override
	public boolean isEnabled() {
		return accountEnabled;
	}

	@Override
	public String getPassword() {
		return null;
	}

	public boolean isAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public void setCredentialsExpired(boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public boolean isAccountEnabled() {
		return accountEnabled;
	}

	public void setAccountEnabled(boolean accountEnabled) {
		this.accountEnabled = accountEnabled;
	}

	public void setPermissions(Set<UserPermission> permissions) {
		this.permissions = permissions;
	}
	
	
	
}
