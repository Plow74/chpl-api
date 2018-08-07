package gov.healthit.chpl;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import gov.healthit.chpl.auth.json.ErrorJSONObject;
import gov.healthit.chpl.auth.user.UserRetrievalException;
import gov.healthit.chpl.domain.error.ObjectMissingValidationErrorJSONObject;
import gov.healthit.chpl.domain.error.ObjectsMissingValidationErrorJSONObject;
import gov.healthit.chpl.domain.error.ValidationErrorJSONObject;
import gov.healthit.chpl.exception.CertificationBodyAccessException;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.exception.InvalidArgumentsException;
import gov.healthit.chpl.exception.MissingReasonException;
import gov.healthit.chpl.exception.ObjectMissingValidationException;
import gov.healthit.chpl.exception.ObjectsMissingValidationException;
import gov.healthit.chpl.exception.ValidationException;
import gov.healthit.chpl.manager.impl.SurveillanceAuthorityAccessDeniedException;
import gov.healthit.chpl.manager.impl.UpdateCertifiedBodyException;
import gov.healthit.chpl.manager.impl.UpdateTestingLabException;

@ControllerAdvice
public class ApiExceptionControllerAdvice {
    private static final Logger LOGGER = LogManager.getLogger(ApiExceptionControllerAdvice.class);

    @ExceptionHandler(EntityRetrievalException.class)
    public ResponseEntity<ErrorJSONObject> exception(final EntityRetrievalException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserRetrievalException.class)
    public ResponseEntity<ErrorJSONObject> exception(final UserRetrievalException e) {
        LOGGER.error(e.getMessage());
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityCreationException.class)
    public ResponseEntity<ErrorJSONObject> exception(final EntityCreationException e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<ErrorJSONObject> exception(final InvalidArgumentsException e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorJSONObject> exception(final IllegalArgumentException e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorJSONObject> typeMismatchException(final TypeMismatchException e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressException.class)
    public ResponseEntity<ErrorJSONObject> exception(final AddressException e) {
        LOGGER.error("Could not send email", e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject("Could not send email. " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UpdateTestingLabException.class)
    public ResponseEntity<ErrorJSONObject> exception(final UpdateTestingLabException e) {
        LOGGER.error("Could not update testing lab - access denied.");
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject("Access Denied"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SurveillanceAuthorityAccessDeniedException.class)
    public ResponseEntity<ErrorJSONObject> exception(final SurveillanceAuthorityAccessDeniedException e) {
        LOGGER.error("Could not update surveillance activity - access denied.");
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject("Access Denied"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UpdateCertifiedBodyException.class)
    public ResponseEntity<ErrorJSONObject> exception(final UpdateCertifiedBodyException e) {
        LOGGER.error("Could not update ACB - access denied.");
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject("Access Denied"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorJSONObject> exception(final MessagingException e) {
        LOGGER.error("Could not send email", e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject("Could not send email. " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorJSONObject> exception(final ValidationException e) {
        ValidationErrorJSONObject error = new ValidationErrorJSONObject();
        error.setErrorMessages(e.getErrorMessages());
        error.setWarningMessages(e.getWarningMessages());
        return new ResponseEntity<ValidationErrorJSONObject>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectsMissingValidationException.class)
    public ResponseEntity<ObjectsMissingValidationErrorJSONObject> exception(
            final ObjectsMissingValidationException e) {
        ObjectsMissingValidationErrorJSONObject errorContainer = new ObjectsMissingValidationErrorJSONObject();
        if (e.getExceptions() != null) {
            for (ObjectMissingValidationException currEx : e.getExceptions()) {
                ObjectMissingValidationErrorJSONObject error = new ObjectMissingValidationErrorJSONObject();
                error.setErrorMessages(currEx.getErrorMessages());
                error.setWarningMessages(currEx.getWarningMessages());
                error.setContact(currEx.getContact());
                error.setObjectId(currEx.getObjectId());
                errorContainer.getErrors().add(error);
            }
        }

        return new ResponseEntity<ObjectsMissingValidationErrorJSONObject>(errorContainer, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectMissingValidationException.class)
    public ResponseEntity<ObjectMissingValidationErrorJSONObject> exception(final ObjectMissingValidationException e) {
        ObjectMissingValidationErrorJSONObject error = new ObjectMissingValidationErrorJSONObject();
        error.setErrorMessages(e.getErrorMessages());
        error.setWarningMessages(e.getWarningMessages());
        error.setContact(e.getContact());
        error.setObjectId(e.getObjectId());
        return new ResponseEntity<ObjectMissingValidationErrorJSONObject>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CertificationBodyAccessException.class)
    public ResponseEntity<ErrorJSONObject> exception(final CertificationBodyAccessException e) {
        LOGGER.error("Caught ACB access exception.", e);
        return new ResponseEntity<ErrorJSONObject>(
                new ErrorJSONObject(e.getMessage() != null ? e.getMessage() : "Unauthorized ACB Access."),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingReasonException.class)
    public ResponseEntity<ErrorJSONObject> exception(final MissingReasonException e) {
        LOGGER.error("Caught missing reason exception.", e);
        return new ResponseEntity<ErrorJSONObject>(
                new ErrorJSONObject(e.getMessage() != null ? e.getMessage() : "A reason is required to perform this action."),
                HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorJSONObject> exception(final Exception e) {
        LOGGER.error("Caught exception.", e);
        return new ResponseEntity<ErrorJSONObject>(new ErrorJSONObject(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
