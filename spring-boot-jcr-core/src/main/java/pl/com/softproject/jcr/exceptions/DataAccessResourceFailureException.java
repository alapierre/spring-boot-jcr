package pl.com.softproject.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class DataAccessResourceFailureException extends DataAccessException {

    public DataAccessResourceFailureException() {
    }

    public DataAccessResourceFailureException(String message) {
        super(message);
    }

    public DataAccessResourceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessResourceFailureException(Throwable cause) {
        super(cause);
    }
}
