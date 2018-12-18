package gov.healthit.chpl.validation.pendingListing.reviewer.edition2015.duplicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductQmsStandardDTO;
import gov.healthit.chpl.util.ErrorMessageUtil;
import gov.healthit.chpl.validation.pendingListing.reviewer.duplicate.DuplicateReviewResult;

@Component("QmsStandard2015DuplicateReviewer")
public class QmsStandard2015DuplicateReviewer {
    private ErrorMessageUtil errorMessageUtil;

    @Autowired
    public QmsStandard2015DuplicateReviewer(final ErrorMessageUtil errorMessageUtil) {
        this.errorMessageUtil = errorMessageUtil;
    }

    public void review(final PendingCertifiedProductDTO listing) {

        DuplicateReviewResult<PendingCertifiedProductQmsStandardDTO> qmsStandardDuplicateResults =
                new DuplicateReviewResult<PendingCertifiedProductQmsStandardDTO>(getPredicate());

        if (listing.getQmsStandards() != null) {
            for (PendingCertifiedProductQmsStandardDTO dto : listing.getQmsStandards()) {
                qmsStandardDuplicateResults.addObject(dto);
            }
        }

        if (qmsStandardDuplicateResults.duplicatesExist()) {
            listing.getWarningMessages().addAll(getWarnings(qmsStandardDuplicateResults.getDuplicateList()));
            listing.setQmsStandards(qmsStandardDuplicateResults.getUniqueList());
        }
    }

    private List<String> getWarnings(final List<PendingCertifiedProductQmsStandardDTO> duplicates) {
        List<String> warnings = new ArrayList<String>();
        for (PendingCertifiedProductQmsStandardDTO duplicate : duplicates) {
            String warning = errorMessageUtil.getMessage("listing.duplicateQmsStandard.2015",
                    duplicate.getName(), duplicate.getApplicableCriteria());
            warnings.add(warning);
        }
        return warnings;
    }

    private BiPredicate<PendingCertifiedProductQmsStandardDTO, PendingCertifiedProductQmsStandardDTO> getPredicate() {
        return new BiPredicate<PendingCertifiedProductQmsStandardDTO, PendingCertifiedProductQmsStandardDTO>() {
            @Override
            public boolean test(final PendingCertifiedProductQmsStandardDTO dto1,
                    final PendingCertifiedProductQmsStandardDTO dto2) {
                if (dto1.getName() != null && dto2.getName() != null
                        && dto1.getApplicableCriteria() != null && dto2.getApplicableCriteria() != null) {
                    return dto1.getName().equals(dto2.getName())
                            && dto1.getApplicableCriteria().equals(dto2.getApplicableCriteria());
                } else {
                    return false;
                }
            }
        };
    }

}
