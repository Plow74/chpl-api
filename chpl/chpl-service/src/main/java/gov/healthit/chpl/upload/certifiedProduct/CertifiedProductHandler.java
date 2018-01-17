package gov.healthit.chpl.upload.certifiedProduct;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.CQMCriterion;
import gov.healthit.chpl.dto.AddressDTO;
import gov.healthit.chpl.dto.CertificationBodyDTO;
import gov.healthit.chpl.dto.CertificationEditionDTO;
import gov.healthit.chpl.dto.CertificationStatusDTO;
import gov.healthit.chpl.dto.ContactDTO;
import gov.healthit.chpl.dto.DeveloperDTO;
import gov.healthit.chpl.dto.PracticeTypeDTO;
import gov.healthit.chpl.dto.ProductClassificationTypeDTO;
import gov.healthit.chpl.dto.ProductDTO;
import gov.healthit.chpl.dto.ProductVersionDTO;
import gov.healthit.chpl.dto.TestingLabDTO;
import gov.healthit.chpl.entity.AddressEntity;
import gov.healthit.chpl.entity.AttestationType;
import gov.healthit.chpl.entity.CertificationCriterionEntity;
import gov.healthit.chpl.entity.listing.pending.PendingCertificationResultEntity;
import gov.healthit.chpl.entity.listing.pending.PendingCertifiedProductEntity;
import gov.healthit.chpl.upload.certifiedProduct.template.TemplateColumnIndexMap;
import gov.healthit.chpl.web.controller.InvalidArgumentsException;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
@Component("certifiedProductHandler")
public abstract class CertifiedProductHandler extends CertifiedProductUploadHandlerImpl {
    private static final Logger LOGGER = LogManager.getLogger(CertifiedProductHandler.class);
    protected static final String PRACTICE_TYPE_AMBULATORY = "AMBULATORY";
    protected static final String PRACTICE_TYPE_INPATIENT = "INPATIENT";
    protected static final String FIRST_ROW_INDICATOR = "NEW";
    protected static final String SUBSEQUENT_ROW_INDICATOR = "SUBELEMENT";
    protected static final String CRITERIA_COL_HEADING_BEGIN = "CRITERIA_";
    
    @Autowired
    MessageSource messageSource;

    public abstract PendingCertifiedProductEntity handle() throws InvalidArgumentsException;
    public abstract TemplateColumnIndexMap getColumnIndexMap();
    public abstract String[] getCriteriaNames();
    
    public String getErrorMessage(String errorField){
    	return String.format(
    			messageSource.getMessage(new DefaultMessageSourceResolvable(errorField),
    					LocaleContextHolder.getLocale()));
    }
    
    public Long getDefaultStatusId() {
        CertificationStatusDTO statusDto = statusDao.getByStatusName("Pending");
        if (statusDto != null) {
            return statusDto.getId();
        }
        return null;
    }

    protected void parseUniqueId(PendingCertifiedProductEntity pendingCertifiedProduct, CSVRecord record) {
        String uniqueId = record.get(getColumnIndexMap().getUniqueIdIndex()).trim();
        pendingCertifiedProduct.setUniqueId(uniqueId);
    }
    
    protected void parseRecordStatus(PendingCertifiedProductEntity pendingCertifiedProduct, CSVRecord record) {
        String status = record.get(getColumnIndexMap().getRecordStatusIndex()).trim();
        pendingCertifiedProduct.setRecordStatus(status);
    }
    
    protected void parsePracticeType(PendingCertifiedProductEntity pendingCertifiedProduct, 
            CSVRecord record) {
    	PracticeTypeDTO foundPracticeType = null;
        String practiceType = record.get(getColumnIndexMap().getPracticeTypeIndex()).trim();
        pendingCertifiedProduct.setPracticeType(practiceType);
        if(!practiceType.equals("")){
        	foundPracticeType = practiceTypeDao.getByName(practiceType);
        }
        if (foundPracticeType != null) {
            pendingCertifiedProduct.setPracticeTypeId(foundPracticeType.getId());
        }    
    }
    
    protected void parseDeveloperProductVersion(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        String developer = record.get(getColumnIndexMap().getDeveloperIndex()).trim();
        String product = record.get(getColumnIndexMap().getProductIndex()).trim();
        String productVersion = record.get(getColumnIndexMap().getVersionIndex()).trim();
        pendingCertifiedProduct.setDeveloperName(developer);
        pendingCertifiedProduct.setProductName(product);
        pendingCertifiedProduct.setProductVersion(productVersion);

        DeveloperDTO foundDeveloper = developerDao.getByName(developer);
        if (foundDeveloper != null) {
            pendingCertifiedProduct.setDeveloperId(foundDeveloper.getId());

            // product
            ProductDTO foundProduct = productDao.getByDeveloperAndName(foundDeveloper.getId(), product);
            if (foundProduct != null) {
                pendingCertifiedProduct.setProductId(foundProduct.getId());

                // version
                ProductVersionDTO foundVersion = versionDao.getByProductAndVersion(foundProduct.getId(),
                        productVersion);
                if (foundVersion != null) {
                    pendingCertifiedProduct.setProductVersionId(foundVersion.getId());
                }
            }
        }
    }
    
    protected void parseDeveloperAddress(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        int devAddressIndex = getColumnIndexMap().getDeveloperAddressStartIndex();
        String developerStreetAddress = record.get(devAddressIndex++).trim();
        String developerState = record.get(devAddressIndex++).trim();
        String developerCity = record.get(devAddressIndex++).trim();
        String developerZipcode = record.get(devAddressIndex++).trim();
        String developerWebsite = record.get(devAddressIndex++).trim();
        String developerEmail = record.get(devAddressIndex++).trim();
        String developerPhone = record.get(devAddressIndex++).trim();
        String developerContactName = record.get(devAddressIndex++).trim();
        pendingCertifiedProduct.setDeveloperStreetAddress(developerStreetAddress);
        pendingCertifiedProduct.setDeveloperCity(developerCity);
        pendingCertifiedProduct.setDeveloperState(developerState);
        pendingCertifiedProduct.setDeveloperZipCode(developerZipcode);
        pendingCertifiedProduct.setDeveloperWebsite(developerWebsite);
        pendingCertifiedProduct.setDeveloperEmail(developerEmail);
        pendingCertifiedProduct.setDeveloperPhoneNumber(developerPhone);
        pendingCertifiedProduct.setDeveloperContactName(developerContactName);

        // look for contact in db
        ContactDTO contactToFind = new ContactDTO();
        contactToFind.setLastName(developerContactName);
        contactToFind.setEmail(developerEmail);
        contactToFind.setPhoneNumber(developerPhone);
        ContactDTO foundContact = contactDao.getByValues(contactToFind);
        if (foundContact != null) {
            pendingCertifiedProduct.setDeveloperContactId(foundContact.getId());
        }

        AddressDTO toFind = new AddressDTO();
        toFind.setStreetLineOne(developerStreetAddress);
        toFind.setCity(developerCity);
        toFind.setState(developerState);
        toFind.setZipcode(developerZipcode);
        AddressDTO foundAddress = addressDao.getByValues(toFind);
        if (foundAddress != null) {
            AddressEntity addressEntity = null;
            try {
                addressEntity = addressDao.getEntityById(foundAddress.getId());
            } catch (final EntityRetrievalException ex) {
                addressEntity = null;
            }
            pendingCertifiedProduct.setDeveloperAddress(addressEntity);
        }
    }
    
    protected void parseEdition(String expected, PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        String certificaitonYear = record.get(getColumnIndexMap().getEditionIndex()).trim();
        pendingCertifiedProduct.setCertificationEdition(certificaitonYear);
        if (!pendingCertifiedProduct.getCertificationEdition().equals(expected.trim())) {
            pendingCertifiedProduct.getErrorMessages()
                    .add("Expecting certification year " + expected.trim() + " but found '"
                            + pendingCertifiedProduct.getCertificationEdition() + "' for product "
                            + pendingCertifiedProduct.getUniqueId());
        }
        CertificationEditionDTO foundEdition = editionDao.getByYear(certificaitonYear);
        if (foundEdition != null) {
            pendingCertifiedProduct.setCertificationEditionId(new Long(foundEdition.getId()));
        }
    }
    
    protected void parseAcbCertificationId(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        pendingCertifiedProduct.setAcbCertificationId(record.get(getColumnIndexMap().getAcbCertificationIdIndex()).trim());
    }
    
    protected void parseAcb(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        String acbName = record.get(getColumnIndexMap().getAcbIndex()).trim();
        pendingCertifiedProduct.setCertificationBodyName(acbName);
        CertificationBodyDTO foundAcb = acbDao.getByName(acbName);
        if (foundAcb != null) {
            pendingCertifiedProduct.setCertificationBodyId(foundAcb.getId());
        } else {
            pendingCertifiedProduct.getErrorMessages()
                    .add("No certification body with name " + acbName + " could be found.");
        }
    }
    
    protected void parseAtl(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        String atlName = record.get(getColumnIndexMap().getAtlIndex()).trim();
        pendingCertifiedProduct.setTestingLabName(atlName);
        TestingLabDTO foundAtl = atlDao.getByName(atlName);
        if (foundAtl != null) {
            pendingCertifiedProduct.setTestingLabId(foundAtl.getId());
        }
    }
    
    protected void parseProductClassification(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        String classification = record.get(getColumnIndexMap().getProductClassificationIndex()).trim();
        pendingCertifiedProduct.setProductClassificationName(classification);
        ProductClassificationTypeDTO foundClassification = classificationDao.getByName(classification);
        if (foundClassification != null) {
            pendingCertifiedProduct.setProductClassificationId(foundClassification.getId());
        }
    }
    
    protected void parseCertificationDate(PendingCertifiedProductEntity pendingCertifiedProduct,
            CSVRecord record) {
        String dateStr = record.get(getColumnIndexMap().getCertificationDateIndex()).trim();
        try {
            Date certificationDate = dateFormatter.parse(dateStr);
            pendingCertifiedProduct.setCertificationDate(certificationDate);
        } catch (final ParseException ex) {
            pendingCertifiedProduct.setCertificationDate(null);
        }
    }
    
    protected void parseHasQms(PendingCertifiedProductEntity pendingCertifiedProduct, CSVRecord record) {
        String hasQmsStr = record.get(getColumnIndexMap().getQmsStartIndex());
        Boolean hasQms = asBoolean(hasQmsStr);
        if (hasQms != null) {
            pendingCertifiedProduct.setHasQms(hasQms.booleanValue());
        }
    }
    
    protected void parseHasIcs(PendingCertifiedProductEntity pendingCertifiedProduct, CSVRecord record) {
        String hasIcsStr = record.get(getColumnIndexMap().getIcsStartIndex()).trim();
        pendingCertifiedProduct.setIcs(asBoolean(hasIcsStr));
    }
    
    protected void parseTransparencyAttestation(PendingCertifiedProductEntity pendingCertifiedProduct, CSVRecord record) {
        //(k)(1) attestation url
        pendingCertifiedProduct.setTransparencyAttestationUrl(record.get(getColumnIndexMap().getK1Index()).trim());
        
        //(k)(2) attestation status
        String k2AttestationStr = record.get(getColumnIndexMap().getK2Index()).trim();
        if (!StringUtils.isEmpty(k2AttestationStr)) {
            if ("0".equals(k2AttestationStr.trim())) {
                pendingCertifiedProduct.setTransparencyAttestation(AttestationType.Negative);
            } else if ("1".equals(k2AttestationStr.trim())) {
                pendingCertifiedProduct.setTransparencyAttestation(AttestationType.Affirmative);
            } else if ("2".equals(k2AttestationStr.trim())) {
                pendingCertifiedProduct.setTransparencyAttestation(AttestationType.NA);
            }
        } else {
            pendingCertifiedProduct.setTransparencyAttestation(null);
        }
    }
    
    public List<CQMCriterion> getApplicableCqmCriterion(List<CQMCriterion> allCqms) {
        List<CQMCriterion> criteria = new ArrayList<CQMCriterion>();
        for (CQMCriterion criterion : allCqms) {
            if (!StringUtils.isEmpty(criterion.getCmsId()) && criterion.getCmsId().startsWith("CMS")) {
                criteria.add(criterion);
            }
        }
        return criteria;
    }
    
    
    /**
     * look up the certification criteria by name and throw an error if we can't
     * find it
     * 
     * @param criterionName
     * @param column
     * @return
     * @throws InvalidArgumentsException
     */
    protected PendingCertificationResultEntity getCertificationResult(String criterionName, String columnValue)
            throws InvalidArgumentsException {
        CertificationCriterionEntity certEntity = certDao.getEntityByName(criterionName);
        if (certEntity == null) {
            throw new InvalidArgumentsException("Could not find a certification criterion matching " + criterionName);
        }

        PendingCertificationResultEntity result = new PendingCertificationResultEntity();
        result.setMappedCriterion(certEntity);
        result.setMeetsCriteria(asBoolean(columnValue));
        return result;
    }
    
    protected Boolean asBooleanEmpty(String value) {
        value = value.trim();

        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return parseBoolean(value);
    }
    
    protected Boolean asBoolean(String value) {
        value = value.trim();

        if (StringUtils.isEmpty(value)) {
            return false;
        }

        return parseBoolean(value);
    }
    
    protected Boolean parseBoolean(String value){
    	// look for a string
        if (value.equalsIgnoreCase("t") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("y")) {
            return true;
        }

        try {
            double numValue = Double.parseDouble(value);
            if (numValue > 0) {
                return true;
            }
        } catch (final NumberFormatException ex) {
            LOGGER.error("Could not parse " + value + " as an integer");
        }

        return false;
    }
}
