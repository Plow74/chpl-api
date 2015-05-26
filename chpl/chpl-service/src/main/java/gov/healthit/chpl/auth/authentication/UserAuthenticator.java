package gov.healthit.chpl.auth.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import gov.healthit.chpl.auth.user.User;
import gov.healthit.chpl.auth.user.UserManager;

public class UserAuthenticator extends BaseUserAuthenticator {

	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserDetailsChecker userDetailsChecker;
	
	@Autowired
	private UserManager userManager;
	
	@Override
	public User getUser(LoginCredentials credentials) throws BadCredentialsException, AccountStatusException {
		
		User user = userManager.getByUserName(credentials.getUserName());
		
		if (checkPassword(credentials.getPassword(), user.getPassword())){
			
			try {
				userDetailsChecker.check(user);
			} catch (AccountStatusException ex) {
				throw ex;
			}
			return user;
			
		} else {
			throw new BadCredentialsException("Bad username and password combination.");
		}
	}

	private boolean checkPassword(String rawPassword, String encodedPassword){
		return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
	}
	
}
