package gov.healthit.chpl.validation.pendingListing.reviewer.edition2014.duplicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.dto.PendingCertificationResultDTO;
import gov.healthit.chpl.dto.PendingCertificationResultTestDataDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.util.ErrorMessageUtil;
import gov.healthit.chpl.validation.pendingListing.reviewer.duplicate.DuplicateReviewResult;

@Component("testData2014DuplicateReviewer")
public class TestData2014DuplicateReviewer {
    private ErrorMessageUtil errorMessageUtil;

    @Autowired
    public TestData2014DuplicateReviewer(final ErrorMessageUtil errorMessageUtil) {
        this.errorMessageUtil = errorMessageUtil;
    }

    public void review(final PendingCertifiedProductDTO listing, final PendingCertificationResultDTO certificationResult) {

        DuplicateReviewResult<PendingCertificationResultTestDataDTO> testDataDuplicateResults =
                new DuplicateReviewResult<PendingCertificationResultTestDataDTO>(getPredicate());

        if (certificationResult.getTestData() != null) {
            for (PendingCertificationResultTestDataDTO dto : certificationResult.getTestData()) {
                testDataDuplicateResults.addObject(dto);
            }
        }

        if (testDataDuplicateResults.duplicatesExist()) {
            listing.getWarningMessages().addAll(
                    getWarnings(testDataDuplicateResults.getDuplicateList(), certificationResult.getNumber()));
            certificationResult.setTestData(testDataDuplicateResults.getUniqueList());
        }
    }

    private List<String> getWarnings(final List<PendingCertificationResultTestDataDTO> duplicates, final String criteria) {
        List<String> warnings = new ArrayList<String>();
        for (PendingCertificationResultTestDataDTO duplicate : duplicates) {
            String warning = errorMessageUtil.getMessage("listing.criteria.duplicateTestData.2014",
                    criteria, duplicate.getVersion());
            warnings.add(warning);
        }
        return warnings;
    }

    private BiPredicate<PendingCertificationResultTestDataDTO, PendingCertificationResultTestDataDTO> getPredicate() {
        return new BiPredicate<PendingCertificationResultTestDataDTO, PendingCertificationResultTestDataDTO>() {
            @Override
            public boolean test(final PendingCertificationResultTestDataDTO dto1,
                    final PendingCertificationResultTestDataDTO dto2) {
                if (dto1.getVersion() != null && dto2.getVersion() != null) {
                    return dto1.getVersion().equals(dto2.getVersion());
                } else {
                    return false;
                }
            }
        };
    }
}
