package FeelGoodApp.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MusicVenueNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleMusicVenueNotFound(MusicVenueNotFoundException exception) {
        ValidationError validationError = new ValidationError("musicVenueId",
                "MusicVenue with id " + exception.getIdNotFound() + " is not found.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleEventNotFound(EventNotFoundException exception) {
        ValidationError validationError = new ValidationError("eventId",
                "Event with id " + exception.getIdNotFound() + " is not found.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventIsNotAvailableForPublicException.class)
    public ResponseEntity<List<ValidationError>> handleEventIsNotAvailableForPublic(EventIsNotAvailableForPublicException exception) {
        ValidationError validationError = new ValidationError("eventId",
                "Event with id " + exception.getIdFound() + " is not available for public.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PerformerNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handlePerformerNotFound(PerformerNotFoundException exception) {
        ValidationError validationError = new ValidationError("performerId",
                "Performer with id " + exception.getIdNotFound() + " is not found.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PerformerFromEventNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handlePerformerFromEventNotFound(PerformerFromEventNotFoundException exception) {
        ValidationError validationError = new ValidationError("performerId",
                "Performer with id " + exception.getPerformerId() +
                        " not found on event with id " + exception.getEventId() + ".");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PerformerNameNotUniqueException.class)
    public ResponseEntity<List<ValidationError>> handlePerformerNameNotUnique(PerformerNameNotUniqueException exception) {
        ValidationError validationError = new ValidationError("name",
                "Performer name already exists");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MusicVenueNameNotUniqueException.class)
    public ResponseEntity<List<ValidationError>> handleMusicVenueNameNotUnique(MusicVenueNameNotUniqueException exception) {
        ValidationError validationError = new ValidationError("name",
                "MusicVenue name already exists");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HasSamePerformerException.class)
    public ResponseEntity<List<ValidationError>> handleHasSamePerformer(HasSamePerformerException exception) {
        ValidationError validationError = new ValidationError("performerId",
                "Performer with id " + exception.getIdFound() + " is already registered for this event.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HeadlinerRemoveException.class)
    public ResponseEntity<List<ValidationError>> handleHeadlinerRemove(HeadlinerRemoveException exception) {
        ValidationError validationError = new ValidationError("eventId, performerId",
                "Performer with id " + exception.getPerformerId() +
                        " is a headliner on event with id " + exception.getEventId() + ". " +
                        "Modify the event date or delete the event.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EventAlreadyHasHeadlinerException.class)
    public ResponseEntity<List<ValidationError>> handleEventAlreadyHasHeadliner(EventAlreadyHasHeadlinerException exception) {
        ValidationError validationError = new ValidationError("eventId",
                "Event with id " + exception.getIdFound() + " has already a headliner performer.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PastEventDateException.class)
    public ResponseEntity<List<ValidationError>> handlePastEvent(PastEventDateException exception) {
        ValidationError validationError = new ValidationError("eventId",
                "Event with id " + exception.getIdFound() + " is a past event.");
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }
}
