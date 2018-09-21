package gov.healthit.chpl.auth.manager.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import gov.healthit.chpl.auth.dao.UserDAO;
import gov.healthit.chpl.auth.dto.UserDTO;
import gov.healthit.chpl.auth.dto.UserPermissionDTO;
import gov.healthit.chpl.auth.entity.UserEntity;
import gov.healthit.chpl.auth.json.User;
import gov.healthit.chpl.auth.json.UserCreationJSONObject;
import gov.healthit.chpl.auth.json.UserInfoJSONObject;
import gov.healthit.chpl.auth.manager.SecuredUserManager;
import gov.healthit.chpl.auth.manager.UserManager;
import gov.healthit.chpl.auth.permission.UserPermissionRetrievalException;
import gov.healthit.chpl.auth.user.UserConversionHelper;
import gov.healthit.chpl.auth.user.UserCreationException;
import gov.healthit.chpl.auth.user.UserManagementException;
import gov.healthit.chpl.auth.user.UserRetrievalException;

/**
 * Implementation of User Manager.
 * @author alarned
 *
 */
@Service
public class UserManagerImpl implements UserManager {
    private static final Logger LOGGER = LogManager.getLogger(UserManagerImpl.class);

    private final Random random = new SecureRandom();
    private static final char[] SYMBOLS;
    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }
        SYMBOLS = tmp.toString().toCharArray();
    }
    private static final int GENERATED_PASSWORD_LENGTH = 15;

    @Autowired private Environment env;

    @Autowired
    private SecuredUserManager securedUserManager;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserDTO create(final UserCreationJSONObject userInfo) throws UserCreationException, UserRetrievalException {

        UserDTO user = UserConversionHelper.createDTO(userInfo);
        Strength strength = getPasswordStrength(user, userInfo.getPassword());
        if (strength.getScore() < UserManager.MIN_PASSWORD_STRENGTH) {
            LOGGER.info("Strength results: [warning: {}] [suggestions: {}] [score: {}] [worst case crack time: {}]",
                    strength.getFeedback().getWarning(),
                    strength.getFeedback().getSuggestions().toString(),
                    strength.getScore(),
                    strength.getCrackTimesDisplay().getOfflineFastHashing1e10PerSecond());
            throw new UserCreationException("Password is not strong enough");
        }
        String encodedPassword = encodePassword(userInfo.getPassword());
        user = securedUserManager.create(user, encodedPassword);
        return user;
    }

    @Override
    @Transactional
    public UserDTO update(final User userInfo) throws UserRetrievalException {

        UserDTO userDTO = getByName(userInfo.getSubjectName());

        if (userInfo.getFullName() != null) {
            userDTO.setFullName(userInfo.getFullName());
        }

        if (userInfo.getFriendlyName() != null) {
            userDTO.setFriendlyName(userInfo.getFriendlyName());
        }

        if (userInfo.getEmail() != null) {
            userDTO.setEmail(userInfo.getEmail());
        }

        if (userInfo.getPhoneNumber() != null) {
            userDTO.setPhoneNumber(userInfo.getPhoneNumber());
        }

        if (userInfo.getAccountEnabled() != null) {
            userDTO.setAccountEnabled(userInfo.getAccountEnabled());
        } else {
            userDTO.setAccountEnabled(true);
        }

        if (userInfo.getAccountLocked() != null) {
            userDTO.setAccountLocked(userInfo.getAccountLocked());
        } else {
            userDTO.setAccountLocked(true);
        }

        if (Boolean.TRUE.equals(userInfo.getComplianceTermsAccepted())) {
            if (userDTO.getComplianceSignatureDate() == null) {
                userDTO.setComplianceSignatureDate(new Date());
            }
        } else {
            userDTO.setComplianceSignatureDate(null);
        }

        userDTO.setTitle(userInfo.getTitle());
        return securedUserManager.update(userDTO);
    }

    @Override
    @Transactional
    public UserDTO update(final UserDTO user) throws UserRetrievalException {
        return securedUserManager.update(user);
    }

    @Transactional
    private void updateContactInfo(final UserEntity user) {
        securedUserManager.updateContactInfo(user);
    }

    @Override
    @Transactional
    public void delete(final UserDTO user)
            throws UserRetrievalException, UserPermissionRetrievalException, UserManagementException {
        securedUserManager.delete(user);
    }

    @Override
    @Transactional
    public void delete(final String userName)
            throws UserRetrievalException, UserPermissionRetrievalException, UserManagementException {

        UserDTO user = securedUserManager.getBySubjectName(userName);
        if (user == null) {
            throw new UserRetrievalException("User not found");
        } else {
            delete(user);
        }
    }

    @Transactional
    public List<UserDTO> getAll() {
        return securedUserManager.getAll();
    }

    @Override
    @Transactional
    public List<UserDTO> getUsersWithPermission(final String permissionName) {
        return securedUserManager.getUsersWithPermission(permissionName);
    }

    @Override
    @Transactional
    public UserDTO getById(final Long id) throws UserRetrievalException {
        return securedUserManager.getById(id);
    }

    @Override
    @Transactional
    public void grantRole(final String userName, final String role)
            throws UserRetrievalException, UserManagementException, UserPermissionRetrievalException {
        securedUserManager.grantRole(userName, role);
    }

    @Override
    @Transactional
    public void grantAdmin(final String userName)
            throws UserPermissionRetrievalException, UserRetrievalException, UserManagementException {
        securedUserManager.grantAdmin(userName);
    }

    @Override
    @Transactional
    public void removeRole(final UserDTO user, final String role)
            throws UserRetrievalException, UserPermissionRetrievalException, UserManagementException {
        securedUserManager.removeRole(user, role);
    }

    @Override
    @Transactional
    public void removeRole(final String userName, final String role)
            throws UserRetrievalException, UserPermissionRetrievalException, UserManagementException {
        securedUserManager.removeRole(userName, role);
    }

    @Override
    @Transactional
    public void removeAdmin(final String userName)
            throws UserRetrievalException, UserPermissionRetrievalException, UserManagementException {
        securedUserManager.removeAdmin(userName);
    }

    @Override
    @Transactional
    public void updateFailedLoginCount(final UserDTO userToUpdate) throws UserRetrievalException {
        securedUserManager.updateFailedLoginCount(userToUpdate);
        String maxLoginsStr = env.getProperty("authMaximumLoginAttempts");
        int maxLogins = Integer.parseInt(maxLoginsStr);

        if (userToUpdate.getFailedLoginCount() >= maxLogins) {
            userToUpdate.setAccountLocked(true);
            securedUserManager.updateAccountLockedStatus(userToUpdate);
        }
    }

    @Override
    @Transactional
    public void updateUserPassword(final String userName, final String password) throws UserRetrievalException {

        String encodedPassword = encodePassword(password);
        UserDTO userToUpdate = securedUserManager.getBySubjectName(userName);
        securedUserManager.updatePassword(userToUpdate, encodedPassword);

    }

    //no auth needed. create a random string and assign it to the user
    @Override
    @Transactional
    public String resetUserPassword(final String username, final String email) throws UserRetrievalException {
        UserDTO foundUser = userDAO.findUserByNameAndEmail(username, email);
        if (foundUser == null) {
            throw new UserRetrievalException("Cannot find user with name " + username + " and email address " + email);
        }

        //create new password
        char[] buf = new char[GENERATED_PASSWORD_LENGTH];

        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = SYMBOLS[random.nextInt(SYMBOLS.length)];
        }
        String password = new String(buf);

        //encode new password
        String encodedPassword = encodePassword(password);

        //update the userDTO with the new password
        userDAO.updatePassword(foundUser.getSubjectName(), encodedPassword);

        return password;
    }

    @Override
    public String encodePassword(final String password) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        return encodedPassword;
    }

    @Override
    public String getEncodedPassword(final UserDTO user) throws UserRetrievalException {
        return userDAO.getEncodedPassword(user);
    }


    @Override
    public Set<UserPermissionDTO> getGrantedPermissionsForUser(final UserDTO user) {
        return securedUserManager.getGrantedPermissionsForUser(user);
    }


    @Override
    public UserDTO getByName(final String userName) throws UserRetrievalException {
        return securedUserManager.getBySubjectName(userName);
    }

    @Override
    public UserInfoJSONObject getUserInfo(final String userName) throws UserRetrievalException {
        UserDTO user = securedUserManager.getBySubjectName(userName);
        UserInfoJSONObject userInfo = new UserInfoJSONObject(user);
        return userInfo;
    }

    @Override
    public Strength getPasswordStrength(final UserDTO user, final String password) {
        ArrayList<String> badWords = new ArrayList<String>();
        badWords.add("chpl");
        badWords.add(user.getEmail());
        badWords.add(user.getFullName());
        badWords.add(user.getFriendlyName());
        badWords.add(user.getUsername());

        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password, badWords);
        return strength;
    }
 }
