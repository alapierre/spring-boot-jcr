package io.alapierre.jcr;

import io.alapierre.jcr.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import io.alapierre.jcr.exceptions.ConcurrencyFailureException;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.version.VersionException;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@Slf4j
public class SessionFactoryUtils {

    public static Session getSession(@NotNull SessionFactory sessionFactory, boolean allowCreate) throws DataAccessResourceFailureException {

        try {

            // check if there is any transaction going on
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
            if (sessionHolder != null && sessionHolder.getSession() != null) {
                log.debug("session from TransactionSynchronizationManager");
                return sessionHolder.getSession();
            }

            if (!allowCreate && !TransactionSynchronizationManager.isSynchronizationActive()) {
                throw new IllegalStateException("No session bound to thread, "
                        + "and configuration does not allow creation of non-transactional one here");
            }

            log.debug("Opening JCR Session");
            Session session = sessionFactory.getSession();

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                log.debug("Registering transaction synchronization for JCR session");
                // Use same session for further JCR actions within the transaction
                // thread object will get removed by synchronization at transaction
                // completion.
                sessionHolder = sessionFactory.getSessionHolder(session);
                sessionHolder.setSynchronizedWithTransaction(true);
                TransactionSynchronizationManager.registerSynchronization(new JcrSessionSynchronization(
                        sessionHolder, sessionFactory));
                TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
            } else {
                log.debug("Synchronization is not active");
            }

            return session;
        } catch (RepositoryException ex) {
            throw new DataAccessResourceFailureException("Could not open Jcr Session", ex);
        }
    }

    public static DataAccessException translateException(RepositoryException ex) {
        if (ex instanceof AccessDeniedException) {
            return new DataRetrievalFailureException("Access denied to this data", ex);
        }
        if (ex instanceof ConstraintViolationException) {
            return new DataIntegrityViolationException("Constraint has been violated", ex);
        }
        if (ex instanceof InvalidItemStateException) {
            return new ConcurrencyFailureException("Invalid item state", ex);
        }
        if (ex instanceof InvalidQueryException) {
            return new DataRetrievalFailureException("Invalid query", ex);
        }
        if (ex instanceof InvalidSerializedDataException) {
            return new DataRetrievalFailureException("Invalid serialized data", ex);
        }
        if (ex instanceof ItemExistsException) {
            return new DataIntegrityViolationException("An item already exists", ex);
        }
        if (ex instanceof ItemNotFoundException) {
            return new DataRetrievalFailureException("Item not found", ex);
        }
        if (ex instanceof LoginException) {
            return new DataAccessResourceFailureException("Bad login", ex);
        }
        if (ex instanceof LockException) {
            return new ConcurrencyFailureException("Item is locked", ex);
        }
        if (ex instanceof MergeException) {
            return new DataIntegrityViolationException("Merge failed", ex);
        }
        if (ex instanceof NamespaceException) {
            return new InvalidDataAccessApiUsageException("Namespace not registred", ex);
        }
        if (ex instanceof NoSuchNodeTypeException) {
            return new InvalidDataAccessApiUsageException("No such node type", ex);
        }
        if (ex instanceof NoSuchWorkspaceException) {
            return new DataAccessResourceFailureException("Workspace not found", ex);
        }
        if (ex instanceof PathNotFoundException) {
            return new DataRetrievalFailureException("Path not found", ex);
        }
        if (ex instanceof ReferentialIntegrityException) {
            return new DataIntegrityViolationException("Referential integrity violated", ex);
        }
        if (ex instanceof UnsupportedRepositoryOperationException) {
            return new InvalidDataAccessApiUsageException("Unsupported operation", ex);
        }
        if (ex instanceof ValueFormatException) {
            return new InvalidDataAccessApiUsageException("Incorrect value format", ex);
        }
        if (ex instanceof VersionException) {
            return new DataIntegrityViolationException("Invalid version graph operation", ex);
        }
        // fallback
        return new JcrSystemException(ex);
    }
}
