package gov.healthit.chpl.dao.impl;


import java.util.Date;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.dao.CertificationCriterionDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dto.CertificationCriterionDTO;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.CHPLTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml")

public class CertificationCriterionDaoTest extends TestCase {

	
	@Autowired
	private CertificationCriterionDAO certificationCriterionDAO;
	
	private static JWTAuthenticatedUser adminUser;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		adminUser = new JWTAuthenticatedUser();
		adminUser.setFirstName("Administrator");
		adminUser.setId(-2L);
		adminUser.setLastName("Administrator");
		adminUser.setSubjectName("admin");
		adminUser.getPermissions().add(new GrantedPermission("ROLE_ADMIN"));
	}
	
	
	@Test
	public void testCreate() throws EntityCreationException, EntityRetrievalException {
		
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		
		CertificationCriterionDTO dto = new CertificationCriterionDTO();
		dto.setAutomatedMeasureCapable(true);
		dto.setAutomatedNumeratorCapable(true);
		dto.setCertificationEditionId(1L);
		dto.setCreationDate(new Date());
		dto.setDeleted(false);
		dto.setDescription("Test");
		//dto.setId(null);
		dto.setLastModifiedDate(new Date());
		dto.setLastModifiedUser(-1L);
		dto.setNumber("CERT123");
		dto.setParentCriterionId(null);
		dto.setRequiresSed(false);
		dto.setTitle("Test Cert Criterion");
		
		CertificationCriterionDTO result = certificationCriterionDAO.create(dto);
		CertificationCriterionDTO check = certificationCriterionDAO.getById(result.getId());
		
		assertEquals(result.getAutomatedMeasureCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getAutomatedNumeratorCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getCertificationEditionId(), check.getCertificationEditionId());
		assertEquals(result.getCreationDate(), check.getCreationDate());
		assertEquals(result.getDeleted(), check.getDeleted());
		assertEquals(result.getDescription(), check.getDescription());
		assertEquals(result.getId(), check.getId());
		assertEquals(result.getLastModifiedUser(), check.getLastModifiedUser());
		assertEquals(result.getNumber(), check.getNumber());
		assertEquals(result.getParentCriterionId(), result.getParentCriterionId());
		assertEquals(result.getRequiresSed(), result.getRequiresSed());
		assertEquals(result.getTitle(), result.getTitle());
		
		certificationCriterionDAO.delete(result.getId());
		
		SecurityContextHolder.getContext().setAuthentication(null);
		
	}

	@Test
	public void testUpdate() throws EntityRetrievalException, EntityCreationException {
		
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		
		CertificationCriterionDTO dto = new CertificationCriterionDTO();
		dto.setAutomatedMeasureCapable(true);
		dto.setAutomatedNumeratorCapable(true);
		dto.setCertificationEditionId(1L);
		dto.setCreationDate(new Date());
		dto.setDeleted(false);
		dto.setDescription("Test");
		dto.setId(null);
		dto.setLastModifiedDate(new Date());
		dto.setLastModifiedUser(-1L);
		dto.setNumber("CERT123");
		dto.setParentCriterionId(null);
		dto.setRequiresSed(false);
		dto.setTitle("Test Cert Criterion");
		
		CertificationCriterionDTO result = certificationCriterionDAO.create(dto);
		
		
		dto.setAutomatedMeasureCapable(false);
		dto.setAutomatedNumeratorCapable(false);
		dto.setCertificationEditionId(2L);
		dto.setCreationDate(new Date());
		dto.setDeleted(false);
		dto.setDescription("Test 1");
		dto.setId(null);
		dto.setLastModifiedDate(new Date());
		dto.setLastModifiedUser(-2L);
		result.setNumber("CERT124");
		dto.setParentCriterionId(null);
		dto.setRequiresSed(true);
		dto.setTitle("Test Cert Criterion 1");
		
		
		certificationCriterionDAO.update(result);
		
		CertificationCriterionDTO check = certificationCriterionDAO.getById(result.getId());
		
		assertEquals(result.getAutomatedMeasureCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getAutomatedNumeratorCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getCertificationEditionId(), check.getCertificationEditionId());
		assertEquals(result.getCreationDate(), check.getCreationDate());
		assertEquals(result.getDeleted(), check.getDeleted());
		assertEquals(result.getDescription(), check.getDescription());
		assertEquals(result.getId(), check.getId());
		assertEquals(result.getLastModifiedUser(), check.getLastModifiedUser());
		assertEquals(result.getNumber(), check.getNumber());
		assertEquals(result.getParentCriterionId(), result.getParentCriterionId());
		assertEquals(result.getRequiresSed(), result.getRequiresSed());
		assertEquals(result.getTitle(), result.getTitle());
		
		certificationCriterionDAO.delete(result.getId());
		
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	public void testDelete() throws EntityCreationException, EntityRetrievalException {
		
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		
		CertificationCriterionDTO dto = new CertificationCriterionDTO();
		dto.setAutomatedMeasureCapable(true);
		dto.setAutomatedNumeratorCapable(true);
		dto.setCertificationEditionId(1L);
		dto.setCreationDate(new Date());
		dto.setDeleted(false);
		dto.setDescription("Test");
		dto.setId(null);
		dto.setLastModifiedDate(new Date());
		dto.setLastModifiedUser(-1L);
		dto.setNumber("CERT123");
		dto.setParentCriterionId(null);
		dto.setRequiresSed(false);
		dto.setTitle("Test Cert Criterion");
		
		CertificationCriterionDTO result = certificationCriterionDAO.create(dto);
		CertificationCriterionDTO check = certificationCriterionDAO.getById(result.getId());
		
		assertEquals(result.getAutomatedMeasureCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getAutomatedNumeratorCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getCertificationEditionId(), check.getCertificationEditionId());
		assertEquals(result.getCreationDate(), check.getCreationDate());
		assertEquals(result.getDeleted(), check.getDeleted());
		assertEquals(result.getDescription(), check.getDescription());
		assertEquals(result.getId(), check.getId());
		assertEquals(result.getLastModifiedUser(), check.getLastModifiedUser());
		assertEquals(result.getNumber(), check.getNumber());
		assertEquals(result.getParentCriterionId(), result.getParentCriterionId());
		assertEquals(result.getRequiresSed(), result.getRequiresSed());
		assertEquals(result.getTitle(), result.getTitle());
		
		certificationCriterionDAO.delete(result.getId());
		
		assertNull(certificationCriterionDAO.getById(result.getId()));
		
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	public void testFindAll() {
		
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		
		assertNotNull(certificationCriterionDAO.findAll());
		
		SecurityContextHolder.getContext().setAuthentication(null);
		
	}
	
	public void testGetById() throws EntityRetrievalException, EntityCreationException {
		
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		
		CertificationCriterionDTO dto = new CertificationCriterionDTO();
		dto.setAutomatedMeasureCapable(true);
		dto.setAutomatedNumeratorCapable(true);
		dto.setCertificationEditionId(1L);
		dto.setCreationDate(new Date());
		dto.setDeleted(false);
		dto.setDescription("Test");
		dto.setId(null);
		dto.setLastModifiedDate(new Date());
		dto.setLastModifiedUser(-1L);
		dto.setNumber("CERT123");
		dto.setParentCriterionId(null);
		dto.setRequiresSed(false);
		dto.setTitle("Test Cert Criterion");
		
		CertificationCriterionDTO result = certificationCriterionDAO.create(dto);
		CertificationCriterionDTO check = certificationCriterionDAO.getById(result.getId());
		
		assertEquals(result.getAutomatedMeasureCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getAutomatedNumeratorCapable(), check.getAutomatedMeasureCapable());
		assertEquals(result.getCertificationEditionId(), check.getCertificationEditionId());
		assertEquals(result.getCreationDate(), check.getCreationDate());
		assertEquals(result.getDeleted(), check.getDeleted());
		assertEquals(result.getDescription(), check.getDescription());
		assertEquals(result.getId(), check.getId());
		assertEquals(result.getLastModifiedUser(), check.getLastModifiedUser());
		assertEquals(result.getNumber(), check.getNumber());
		assertEquals(result.getParentCriterionId(), result.getParentCriterionId());
		assertEquals(result.getRequiresSed(), result.getRequiresSed());
		assertEquals(result.getTitle(), result.getTitle());
		
		certificationCriterionDAO.delete(result.getId());
		
		SecurityContextHolder.getContext().setAuthentication(null);
	}

}