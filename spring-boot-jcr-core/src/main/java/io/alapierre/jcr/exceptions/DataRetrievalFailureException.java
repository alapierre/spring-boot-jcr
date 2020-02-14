package io.alapierre.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class DataRetrievalFailureException extends DataAccessException {
    public DataRetrievalFailureException() {
    }

    public DataRetrievalFailureException(String message) {
        super(message);
    }

    public DataRetrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataRetrievalFailureException(Throwable cause) {
        super(cause);
    }
}
