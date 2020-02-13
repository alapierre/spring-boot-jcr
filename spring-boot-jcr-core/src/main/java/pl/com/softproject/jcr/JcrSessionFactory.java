package pl.com.softproject.jcr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.jcr.Credentials;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.ObservationManager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;



/**
 *
 * based on https://github.com/astubbs/spring-modules/
 * Created 12.02.2020 copyright original authors 2020
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@Slf4j
public class JcrSessionFactory implements InitializingBean, DisposableBean, SessionFactory {

    @Getter
    private Repository repository;

    @Getter
    private String workspaceName;

    private Credentials credentials;

    /**
     * Constructor with all the required fields.
     *
     * @param repository JCR repository
     * @param workspaceName optional workspace name, can by null
     * @param credentials mandatory credentials for connect to JCR repository
     */
    public JcrSessionFactory(@NotNull Repository repository, @Nullable String workspaceName, @NotNull Credentials credentials) {
        this.repository = repository;
        this.workspaceName = workspaceName;
        this.credentials = credentials;
    }

    public void afterPropertiesSet() throws Exception {

        // TODO: dla obsługi multi tenant należy użyć namespace lub workspace
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        // do nothing yet
    }

    /**
     * @see SessionFactory#getSession()
     */
    public Session getSession() throws RepositoryException {
        return repository.login(credentials, workspaceName);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "SessionFactory for " +
                getRepositoryInfo() +
                "|workspace=" +
                workspaceName;
    }

    /**
     * A toString representation of the Repository.
     */
    private String getRepositoryInfo() {
        // in case toString() is called before afterPropertiesSet()
        if (getRepository() == null)
            return "<N/A>";

        return getRepository().getDescriptor(Repository.REP_NAME_DESC) +
                " " +
                getRepository().getDescriptor(Repository.REP_VERSION_DESC);
    }

    @Override
    public SessionHolder getSessionHolder(@NotNull Session session) {
        return new SessionHolder(session);
    }


}
