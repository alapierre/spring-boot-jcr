package io.alapierre.jcr;

import org.jetbrains.annotations.NotNull;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public interface SessionFactory {

    /**
     * Returns a JCR Session using the credentials and workspace on this JcrSessionFactory.
     * The session factory doesn't allow specification of a different workspace name because:
     * <p>
     * " Each Session object is associated one-to-one with a Workspace object. The Workspace
     * object represents a `view` of an actual repository workspace entity as seen through
     * the authorization settings of its associated Session." (quote from javax.jcr.Session javadoc).
     * </p>
     *
     * @return the JCR session.
     * @throws RepositoryException
     */
    Session getSession() throws RepositoryException;

    SessionHolder getSessionHolder(@NotNull Session session);
}
