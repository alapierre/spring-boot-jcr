package io.alapierre.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class ConcurrencyFailureException extends DataAccessException {
    public ConcurrencyFailureException() {
    }

    public ConcurrencyFailureException(String message) {
        super(message);
    }

    public ConcurrencyFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrencyFailureException(Throwable cause) {
        super(cause);
    }
}
