package io.alapierre.jcr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.jcr.Session;

/**
 * Created 13.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@Slf4j
class JcrSessionSynchronization extends TransactionSynchronizationAdapter {

    private final SessionHolder sessionHolder;

    private final SessionFactory sessionFactory;

    private boolean holderActive = true;

    public JcrSessionSynchronization(SessionHolder holder, SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        sessionHolder = holder;
    }

    @Override
    public void suspend() {
        if (this.holderActive) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
        }
    }

    @Override
    public void resume() {
        if (this.holderActive) {
            TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
        }
    }

    @Override
    public void beforeCompletion() {
        TransactionSynchronizationManager.unbindResource(sessionFactory);
        this.holderActive = false;
        releaseSession(sessionHolder.getSession(), this.sessionFactory);
    }

    public static void releaseSession(Session session, SessionFactory sessionFactory) {
        if (session == null) {
            return;
        }
        // Only close non thread bound Sessions.
        if (!isSessionThreadBound(session, sessionFactory)) {
            log.debug("Closing JCR Session");
            session.logout();
        }
    }

    public static boolean isSessionThreadBound(Session session, SessionFactory sessionFactory) {
        if (sessionFactory == null) {
            return false;
        }
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
        return (sessionHolder != null && session == sessionHolder.getSession());
    }

}
