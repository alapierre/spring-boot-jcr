package pl.com.softproject.jcr;

import org.springframework.transaction.support.ResourceHolderSupport;

import javax.jcr.Session;

/**
 * Created 13.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
public class SessionHolder extends ResourceHolderSupport {

    private Session session;

    public SessionHolder(Session session) {
        setSession(session);
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    /**
     * @see org.springframework.transaction.support.ResourceHolderSupport#clear()
     */
    public void clear() {
        super.clear();
        session = null;
    }

}
