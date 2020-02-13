package pl.com.softproject.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;

/**
 * Callback interface for Jcr code. To be used with JcrTemplate's execute method
 *
 * Created 12.02.2020 copyright original authors 2020
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@FunctionalInterface
public interface JcrCallback<T> {

    /**
     * Called by {@link JcrTemplate#execute} within an active JCR
     * {@link javax.jcr.Session}. It is not responsible for logging
     * out of the <code>Session</code> or handling transactions.
     */
    T doInJcr(Session session) throws IOException, RepositoryException;

}
