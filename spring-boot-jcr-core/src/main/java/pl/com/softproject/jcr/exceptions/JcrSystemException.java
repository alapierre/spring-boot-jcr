package pl.com.softproject.jcr.exceptions;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class JcrSystemException extends UncategorizedDataAccessException {
    public JcrSystemException() {
    }

    public JcrSystemException(String message) {
        super(message);
    }

    public JcrSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public JcrSystemException(Throwable cause) {
        super(cause);
    }
}
