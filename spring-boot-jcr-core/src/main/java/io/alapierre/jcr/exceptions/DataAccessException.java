package io.alapierre.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
