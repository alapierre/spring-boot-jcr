package pl.com.softproject.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class UncategorizedDataAccessException extends DataAccessException {
    public UncategorizedDataAccessException() {
    }

    public UncategorizedDataAccessException(String message) {
        super(message);
    }

    public UncategorizedDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncategorizedDataAccessException(Throwable cause) {
        super(cause);
    }
}
