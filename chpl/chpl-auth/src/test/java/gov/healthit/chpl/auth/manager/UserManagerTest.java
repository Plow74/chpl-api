package gov.healthit.chpl.auth.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.authentication.Authenticator;
import gov.healthit.chpl.auth.authentication.JWTUserConverter;
import gov.healthit.chpl.auth.dto.UserDTO;
import gov.healthit.chpl.auth.dto.UserPermissionDTO;
import gov.healthit.chpl.auth.json.UserCreationJSONObject;
import gov.healthit.chpl.auth.json.UserInfoJSONObject;
import gov.healthit.chpl.auth.jwt.JWTCreationException;
import gov.healthit.chpl.auth.jwt.JWTValidationException;
import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.permission.UserPermissionRetrievalException;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.auth.user.User;
import gov.healthit.chpl.auth.user.UserCreationException;
import gov.healthit.chpl.auth.user.UserManagementException;
import gov.healthit.chpl.auth.user.UserRetrievalException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.auth.CHPLAuthenticationSecurityTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml")
public class UserManagerTest {

    @Autowired
    private UserManager userManager;

    @Autowired
    private Authenticator userAuthenticator;

    @Autowired
    private JWTUserConverter jwtUserConverter;

    private static JWTAuthenticatedUser adminUser;

    @BeforeClass
    public static void setUpClass() throws Exception {
        adminUser = new JWTAuthenticatedUser();
        adminUser.setFullName("Administrator");
        adminUser.setId(-2L);
        adminUser.setFriendlyName("Administrator");
        adminUser.setSubjectName("admin");
        adminUser.getPermissions().add(new GrantedPermission("ROLE_ADMIN"));
    }

    @Test(expected = UserRetrievalException.class)
    public void testCreateDeleteUser() throws UserCreationException, UserRetrievalException,
    UserPermissionRetrievalException, UserManagementException {

        SecurityContextHolder.getContext().setAuthentication(adminUser);

        UserCreationJSONObject toCreate = new UserCreationJSONObject();
        toCreate.setEmail("email@example.com");
        toCreate.setFullName("test");
        toCreate.setFriendlyName("test");
        toCreate.setPassword("a long password that should be good enough to not be blocked");
        toCreate.setPhoneNumber("123-456-7890");
        toCreate.setSubjectName("testuser");
        toCreate.setTitle("Dr.");

        UserDTO result = userManager.create(toCreate);
        UserDTO created = userManager.getById(result.getId());

        assertEquals(toCreate.getEmail(), created.getEmail());
        assertEquals(toCreate.getFullName(), created.getFullName());
        assertEquals(toCreate.getFriendlyName(), created.getFriendlyName());
        assertEquals(toCreate.getSubjectName(), created.getSubjectName());

        userManager.delete(created);
        userManager.getById(result.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test(expected = UserRetrievalException.class)
    public void testCreateDeleteUserByUsername() throws UserCreationException,
    UserRetrievalException, UserPermissionRetrievalException, UserManagementException{

        SecurityContextHolder.getContext().setAuthentication(adminUser);

        UserCreationJSONObject toCreate = new UserCreationJSONObject();
        toCreate.setEmail("email@example.com");
        toCreate.setFullName("test");
        toCreate.setFriendlyName("test");
        toCreate.setPassword("a long password that should be good enough to not be blocked");
        toCreate.setPhoneNumber("123-456-7890");
        toCreate.setSubjectName("testuser");
        toCreate.setTitle("Dr.");

        UserDTO result = userManager.create(toCreate);
        UserDTO created = userManager.getById(result.getId());

        assertEquals(toCreate.getEmail(), created.getEmail());
        assertEquals(toCreate.getFullName(), created.getFullName());
        assertEquals(toCreate.getFriendlyName(), created.getFriendlyName());
        assertEquals(toCreate.getSubjectName(), created.getSubjectName());

        userManager.delete(created.getUsername());
        userManager.getById(result.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Verify that a weak password results in a Create User exception.
     * @throws UserCreationException expected
     * @throws UserRetrievalException not expected
     * @throws UserPermissionRetrievalException not expected
     * @throws UserManagementException not expected
     */
    @Test(expected = UserCreationException.class)
    public void testCreateDeleteUserWithBadPassword() throws UserCreationException,
    UserRetrievalException, UserPermissionRetrievalException, UserManagementException {

        SecurityContextHolder.getContext().setAuthentication(adminUser);

        UserCreationJSONObject toCreate = new UserCreationJSONObject();
        toCreate.setEmail("email@example.com");
        toCreate.setFullName("test");
        toCreate.setFriendlyName("test");
        toCreate.setPassword("weak");
        toCreate.setPhoneNumber("123-456-7890");
        toCreate.setSubjectName("testuser");
        toCreate.setTitle("Dr.");

        UserDTO result = userManager.create(toCreate);
        UserDTO created = userManager.getById(result.getId());

        assertEquals(toCreate.getEmail(), created.getEmail());
        assertEquals(toCreate.getFullName(), created.getFullName());
        assertEquals(toCreate.getFriendlyName(), created.getFriendlyName());
        assertEquals(toCreate.getSubjectName(), created.getSubjectName());

        userManager.delete(created.getUsername());
        userManager.getById(result.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testUpdateUser() throws UserRetrievalException {

        SecurityContextHolder.getContext().setAuthentication(adminUser);

        //public UserDTO update(User userInfo) throws UserRetrievalException;
        gov.healthit.chpl.auth.json.User userInfo = new gov.healthit.chpl.auth.json.User();
        userInfo.setSubjectName("testUser2");
        userInfo.setFullName("firstName");
        userInfo.setFriendlyName("lastName");

        userManager.update(userInfo);

        UserDTO updated = userManager.getByName("testUser2");
        assertEquals(userInfo.getFullName(), updated.getFullName());
        assertEquals(userInfo.getFriendlyName(), updated.getFriendlyName());

        SecurityContextHolder.getContext().setAuthentication(null);

    }

    @Test
    public void testGetAll(){
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        List<UserDTO> results = userManager.getAll();
        assertEquals(results.size(), 3);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testGetById() throws UserRetrievalException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        UserDTO result = userManager.getById(-2L);
        assertEquals(result.getSubjectName(), "admin");
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test(expected=UserRetrievalException.class)
    public void testGetByIdNotFound() throws UserRetrievalException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        userManager.getById(-6000L);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testGetByName() throws UserRetrievalException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        UserDTO result = userManager.getByName("admin");
        assertEquals(-2L, (long) result.getId());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testGetUserInfo() throws UserRetrievalException {

        SecurityContextHolder.getContext().setAuthentication(adminUser);
        UserInfoJSONObject userInfo = userManager.getUserInfo("admin");
        assertEquals(userInfo.getUser().getSubjectName(), "admin");
        assertEquals(userInfo.getUser().getFullName(), "Administrator");
        assertEquals(userInfo.getUser().getEmail(), "info@ainq.com");

    }

    @Test
    public void testGrantRole() throws UserRetrievalException,
    UserManagementException, UserPermissionRetrievalException{

        SecurityContextHolder.getContext().setAuthentication(adminUser);
        UserDTO before = userManager.getByName("testUser2");
        Set<UserPermissionDTO> beforeRoles = getUserPermissions(before);
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        userManager.grantRole("testUser2", "ROLE_ACB");

        UserDTO after = userManager.getByName("testUser2");

        Set<UserPermissionDTO> afterRoles = getUserPermissions(after);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        assertEquals(afterRoles.size(), 2);
        assertEquals(beforeRoles.size(), 1);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testGrantAdmin() throws UserRetrievalException,
    UserPermissionRetrievalException, UserManagementException, JWTCreationException, JWTValidationException{

        SecurityContextHolder.getContext().setAuthentication(adminUser);
        UserDTO before = userManager.getByName("testUser2");
        Set<UserPermissionDTO> beforeRoles = getUserPermissions(before);
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        userManager.grantAdmin("testUser2");

        UserDTO after = userManager.getByName("testUser2");

        Set<UserPermissionDTO> afterRoles = getUserPermissions(after);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        assertEquals(afterRoles.size(), 2);
        assertEquals(beforeRoles.size(), 1);

        String jwt = userAuthenticator.getJWT(after);
        User newAdmin = jwtUserConverter.getAuthenticatedUser(jwt);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        UserDTO testUser = userManager.getByName("TESTUSER");
        Set<UserPermissionDTO> testUserRolesBefore = getUserPermissions(testUser);

        // set the new admin user as the current Authentication
        SecurityContextHolder.getContext().setAuthentication(newAdmin);

        userManager.grantAdmin("TESTUSER"); // If we can do this, we have Admin privileges.

        Set<UserPermissionDTO> testUserRolesAfter = getUserPermissions(testUser);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        assertTrue(testUserRolesAfter.size() > testUserRolesBefore.size());
        SecurityContextHolder.getContext().setAuthentication(null);

    }

    @Test
    public void testRemoveRole() throws UserRetrievalException,
    UserPermissionRetrievalException, UserManagementException{

        SecurityContextHolder.getContext().setAuthentication(adminUser);
        userManager.grantRole("testUser2", "ROLE_ACB");

        UserDTO after = userManager.getByName("testUser2");

        Set<UserPermissionDTO> afterRoles = getUserPermissions(after);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        assertEquals(afterRoles.size(),2);

        userManager.removeRole("testUser2", "ROLE_ACB");
        Set<UserPermissionDTO> afterRemovedRoles = getUserPermissions(after);

        assertEquals(afterRemovedRoles.size(),1);
        SecurityContextHolder.getContext().setAuthentication(null);

    }

    @Test
    public void testRemoveRoleDTO() throws UserRetrievalException,
    UserPermissionRetrievalException, UserManagementException{


        SecurityContextHolder.getContext().setAuthentication(adminUser);
        userManager.grantRole("testUser2", "ROLE_ACB");

        UserDTO after = userManager.getByName("testUser2");

        Set<UserPermissionDTO> afterRoles = getUserPermissions(after);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        assertEquals(afterRoles.size(), 2);

        userManager.removeRole(after, "ROLE_ACB");
        Set<UserPermissionDTO> afterRemovedRoles = getUserPermissions(after);

        assertEquals(afterRemovedRoles.size(), 1);
        SecurityContextHolder.getContext().setAuthentication(null);

    }

    @Test
    public void testRemoveAdmin() throws UserRetrievalException,
    UserPermissionRetrievalException, UserManagementException, JWTCreationException, JWTValidationException{

        SecurityContextHolder.getContext().setAuthentication(adminUser);
        UserDTO before = userManager.getByName("testUser2");
        Set<UserPermissionDTO> beforeRoles = getUserPermissions(before);
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        userManager.grantAdmin("testUser2");

        UserDTO after = userManager.getByName("testUser2");

        Set<UserPermissionDTO> afterRoles = getUserPermissions(after);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        assertEquals(afterRoles.size(), 2);
        assertEquals(beforeRoles.size(), 1);

        String jwt = userAuthenticator.getJWT(after);
        User newAdmin = jwtUserConverter.getAuthenticatedUser(jwt);
        SecurityContextHolder.getContext().setAuthentication(adminUser);

        UserDTO testUser = userManager.getByName("TESTUSER");
        Set<UserPermissionDTO> testUserRolesBefore = getUserPermissions(testUser);

        // set the new admin user as the current Authentication
        SecurityContextHolder.getContext().setAuthentication(newAdmin);

        userManager.grantAdmin("TESTUSER"); // If we can do this, we have Admin privileges.

        Set<UserPermissionDTO> testUserRolesAfter = getUserPermissions(testUser);
        SecurityContextHolder.getContext().setAuthentication(newAdmin);

        assertTrue(testUserRolesAfter.size() > testUserRolesBefore.size());

        userManager.removeAdmin("TESTUSER");
        userManager.removeAdmin("testUser2");


        UserDTO unPrivileged = userManager.getByName("testUser2");

        String unPrivilegedJwt = userAuthenticator.getJWT(unPrivileged);
        User nonAdmin = jwtUserConverter.getAuthenticatedUser(unPrivilegedJwt);

        SecurityContextHolder.getContext().setAuthentication(nonAdmin);

        Boolean grantFailed = false;

        try {
            userManager.grantAdmin("TESTUSER");
        } catch (Exception e) {
            grantFailed = true;
        }
        SecurityContextHolder.getContext().setAuthentication(null);

        assertTrue(grantFailed);
    }

    private Set<UserPermissionDTO> getUserPermissions(final UserDTO user) {

        Authentication authenticator = new Authentication() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
                auths.add(new GrantedPermission("ROLE_USER_AUTHENTICATOR"));
                return auths;
            }

            @Override
            public Object getCredentials(){
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal(){
                return null;
            }
            @Override
            public boolean isAuthenticated(){
                return true;
            }

            @Override
            public void setAuthenticated(final boolean arg0) throws IllegalArgumentException {}

            @Override
            public String getName(){
                return "AUTHENTICATOR";
            }

        };
        SecurityContextHolder.getContext().setAuthentication(authenticator);
        Set<UserPermissionDTO> permissions = userManager.getGrantedPermissionsForUser(user);
        SecurityContextHolder.getContext().setAuthentication(null);

        return permissions;
    }
}
