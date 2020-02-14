package io.alapierre.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class InvalidDataAccessApiUsageException extends DataAccessException {
    public InvalidDataAccessApiUsageException() {
    }

    public InvalidDataAccessApiUsageException(String message) {
        super(message);
    }

    public InvalidDataAccessApiUsageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataAccessApiUsageException(Throwable cause) {
        super(cause);
    }
}
