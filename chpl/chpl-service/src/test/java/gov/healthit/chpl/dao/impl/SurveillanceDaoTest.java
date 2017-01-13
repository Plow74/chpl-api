package gov.healthit.chpl.dao.impl;


import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.caching.UnitTestRules;
import gov.healthit.chpl.dao.CertifiedProductDAO;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.SurveillanceDAO;
import gov.healthit.chpl.domain.CertifiedProduct;
import gov.healthit.chpl.domain.Surveillance;
import gov.healthit.chpl.domain.SurveillanceNonconformity;
import gov.healthit.chpl.domain.SurveillanceNonconformityStatus;
import gov.healthit.chpl.domain.SurveillanceRequirement;
import gov.healthit.chpl.domain.SurveillanceRequirementType;
import gov.healthit.chpl.domain.SurveillanceResultType;
import gov.healthit.chpl.domain.SurveillanceType;
import gov.healthit.chpl.dto.CertifiedProductDTO;
import junit.framework.TestCase;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.CHPLTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml")
public class SurveillanceDaoTest extends TestCase {
	
	@Autowired
	private SurveillanceDAO survDao;
	@Autowired
	private CertifiedProductDAO cpDao;
	
	@Rule
    @Autowired
    public UnitTestRules cacheInvalidationRule;
	
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
	@Transactional
	@Rollback(true)
	public void insertSurveillanceWithoutNonconformities() throws EntityRetrievalException {
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Surveillance surv = new Surveillance();
		
		CertifiedProductDTO cpDto = cpDao.getById(1L);
		CertifiedProduct cp = new CertifiedProduct();
		cp.setId(cpDto.getId());
		cp.setChplProductNumber(cp.getChplProductNumber());
		cp.setEdition(cp.getEdition());
		surv.setCertifiedProduct(cp);
		surv.setStartDate(new Date());
		surv.setRandomizedSitesUsed(10);
		SurveillanceType type = survDao.findSurveillanceType("Randomized");
		surv.setType(type);
		
		SurveillanceRequirement req = new SurveillanceRequirement();
		req.setRequirement("170.314 (a)(1)");
		SurveillanceRequirementType reqType = survDao.findSurveillanceRequirementType("Certified Capability");
		req.setType(reqType);
		SurveillanceResultType resType = survDao.findSurveillanceResultType("No Non-Conformity");
		req.setResult(resType);
		
		surv.getRequirements().add(req);
		
		Long insertedId = survDao.insertSurveillance(surv);
		assertNotNull(insertedId);
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void insertSurveillanceWithNonconformities() throws EntityRetrievalException {
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Surveillance surv = new Surveillance();
		
		CertifiedProductDTO cpDto = cpDao.getById(1L);
		CertifiedProduct cp = new CertifiedProduct();
		cp.setId(cpDto.getId());
		cp.setChplProductNumber(cp.getChplProductNumber());
		cp.setEdition(cp.getEdition());
		surv.setCertifiedProduct(cp);
		surv.setStartDate(new Date());
		surv.setRandomizedSitesUsed(10);
		SurveillanceType type = survDao.findSurveillanceType("Randomized");
		surv.setType(type);
		
		SurveillanceRequirement req = new SurveillanceRequirement();
		req.setRequirement("170.314 (a)(1)");
		SurveillanceRequirementType reqType = survDao.findSurveillanceRequirementType("Certified Capability");
		req.setType(reqType);
		SurveillanceResultType resType = survDao.findSurveillanceResultType("No Non-Conformity");
		req.setResult(resType);
		surv.getRequirements().add(req);

		SurveillanceRequirement req2 = new SurveillanceRequirement();
		req2.setRequirement("170.314 (a)(2)");
		reqType = survDao.findSurveillanceRequirementType("Certified Capability");
		req2.setType(reqType);
		resType = survDao.findSurveillanceResultType("Non-Conformity");
		req2.setResult(resType);
		surv.getRequirements().add(req2);
		
		SurveillanceNonconformity nc = new SurveillanceNonconformity();
		nc.setCapApprovalDate(new Date());
		nc.setCapMustCompleteDate(new Date());
		nc.setCapStartDate(new Date());
		nc.setDateOfDetermination(new Date());
		nc.setDeveloperExplanation("Something");
		nc.setFindings("Findings!");
		nc.setSitesPassed(2);
		nc.setNonconformityType("170.314 (a)(2)");
		nc.setSummary("summary");
		nc.setTotalSites(5);
		SurveillanceNonconformityStatus ncStatus = survDao.findSurveillanceNonconformityStatusType("Open");
		nc.setStatus(ncStatus);
		req2.getNonconformities().add(nc);
		
		Long insertedId = survDao.insertSurveillance(surv);
		assertNotNull(insertedId);
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void updateSurveillanceWithoutNonconformities() throws EntityRetrievalException {
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		Surveillance surv = new Surveillance();
		
		CertifiedProductDTO cpDto = cpDao.getById(1L);
		CertifiedProduct cp = new CertifiedProduct();
		cp.setId(cpDto.getId());
		cp.setChplProductNumber(cp.getChplProductNumber());
		cp.setEdition(cp.getEdition());
		surv.setCertifiedProduct(cp);
		surv.setStartDate(new Date());
		surv.setRandomizedSitesUsed(10);
		SurveillanceType type = survDao.findSurveillanceType("Randomized");
		surv.setType(type);
		
		SurveillanceRequirement req = new SurveillanceRequirement();
		req.setRequirement("170.314 (a)(1)");
		SurveillanceRequirementType reqType = survDao.findSurveillanceRequirementType("Certified Capability");
		req.setType(reqType);
		SurveillanceResultType resType = survDao.findSurveillanceResultType("No Non-Conformity");
		req.setResult(resType);
		
		surv.getRequirements().add(req);
		
		Long insertedId = survDao.insertSurveillance(surv);
		assertNotNull(insertedId);
		
		surv.setId(insertedId);
		surv.setEndDate(new Date());
		survDao.updateSurveillance(surv);
		
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	@Transactional
	public void testGetSurveillanceTypeRandomized() {
		SurveillanceType result = survDao.findSurveillanceType("Randomized");
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals("Randomized", result.getName());
	}
	
	@Test
	@Transactional
	public void testGetSurveillanceTypeRandomizedCaseInsensitive() {
		SurveillanceType result = survDao.findSurveillanceType("rANdOmIzED");
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals("Randomized", result.getName());
	}
	
	@Test
	@Transactional
	public void testGetAllSurveillanceTypes() {
		List<SurveillanceType> results = survDao.getAllSurveillanceTypes();
		assertNotNull(results);
		assertEquals(2, results.size());
		for(SurveillanceType result : results) {
			assertNotNull(result.getId());
			assertNotNull(result.getName());
			assertTrue(result.getName().length() > 0);
		}
	}
	
	@Test
	@Transactional
	public void testGetAllSurveillanceResultTypes() {
		List<SurveillanceResultType> results = survDao.getAllSurveillanceResultTypes();
		assertNotNull(results);
		assertEquals(2, results.size());
		for(SurveillanceResultType result : results) {
			assertNotNull(result.getId());
			assertNotNull(result.getName());
			assertTrue(result.getName().length() > 0);
		}
	}
	
	@Test
	@Transactional
	public void testGetAllSurveillanceRequirementTypes() {
		List<SurveillanceRequirementType> results = survDao.getAllSurveillanceRequirementTypes();
		assertNotNull(results);
		assertEquals(3, results.size());
		for(SurveillanceRequirementType result : results) {
			assertNotNull(result.getId());
			assertNotNull(result.getName());
			assertTrue(result.getName().length() > 0);
		}
	}
	
	@Test
	@Transactional
	public void testGetAllNonconformityStatusTypes() {
		List<SurveillanceNonconformityStatus> results = survDao.getAllSurveillanceNonconformityStatusTypes();
		assertNotNull(results);
		assertEquals(2, results.size());
		for(SurveillanceNonconformityStatus result : results) {
			assertNotNull(result.getId());
			assertNotNull(result.getName());
			assertTrue(result.getName().length() > 0);
		}
	}
}
