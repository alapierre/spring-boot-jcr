package io.alapierre.jcr;

import io.alapierre.jcr.exceptions.DataAccessException;
import io.alapierre.jcr.exceptions.JcrSystemException;
import io.alapierre.jcr.exceptions.UncategorizedDataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.commons.JcrUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.ContentHandler;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.alapierre.jcr.SessionFactoryUtils.translateException;

/**
 * Created 12.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@Slf4j
@SuppressWarnings("unused")
public class JcrTemplate {

    private final SessionFactory sessionFactory;

    private final TenantProvider tenantProvider;

    public JcrTemplate(SessionFactory sessionFactory, TenantProvider tenantProvider) {
        this.sessionFactory = sessionFactory;
        this.tenantProvider = tenantProvider;
    }

    public <T> T execute(JcrCallback<T> action) throws DataAccessException {
        Session session = null;
        // TODO: implements thread bound session support
        try {
            // TODO: does flushing (session.refresh) should work here?
            // flushIfNecessary(session, existingTransaction);
            session = getSession();
            return action.doInJcr(session);
        } catch (RepositoryException ex) {
            throw translateException(ex);
            // IOException are not converted here
        } catch (IOException | RuntimeException ex) {
            throw new UncategorizedDataAccessException(ex);
        } finally {
            log.debug("closing session for thread: {}", Thread.currentThread().getName());
            if(session != null)
                session.logout();
        }
    }

    protected Session getSession() throws DataAccessException, RepositoryException {
        log.debug("get session from SessionFactoryUtils");
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);

        Optional<String> tenant = tenantProvider.getTenant();
        if (tenant.isPresent()) {
            log.info("Get session for tenantName: [{}]", tenant.get());
            session = session.getRepository().login(tenant.get());
        }

        return session;
    }

    public void addLockToken(final String lock) {
        execute(session -> {
            session.getWorkspace().getLockManager().addLockToken(lock);
            return null;
        });
    }

    public Object getAttribute(final String name) {
        return execute(session -> session.getAttribute(name));
    }

    public String[] getAttributeNames() {
        return execute(Session::getAttributeNames);
    }

    public ContentHandler getImportContentHandler(final String parentAbsPath, final int uuidBehavior) {
        return execute(session -> session.getImportContentHandler(parentAbsPath, uuidBehavior));
    }

    public Item getItem(final String absPath) {
        return execute(session -> session.getItem(absPath));
    }

    public String[] getLockTokens() {
        return execute(session -> session.getWorkspace().getLockManager().getLockTokens());
    }

    public Node getNodeByIdentifier(final String identifier) {
        return execute( session -> session.getNodeByIdentifier(identifier));
    }

    public Node getRootNode() {
        return execute(Session::getRootNode);
    }

    public String getUserID() {
        return execute(Session::getUserID);
    }

    public ValueFactory getValueFactory() {
        return execute(Session::getValueFactory);
    }

    public boolean hasPendingChanges() {
        return execute(Session::hasPendingChanges);
    }

    public void importXML(final String parentAbsPath, final InputStream in, final int uuidBehavior) {
        execute(session -> {
            try {
                session.importXML(parentAbsPath, in, uuidBehavior);
            }
            catch (IOException e) {
                throw new JcrSystemException(e);
            }
            return null;
        });
    }

    public void refresh(final boolean keepChanges) {
        execute(session -> {
            session.refresh(keepChanges);
            return null;
        });
    }

    public void rename(final Node node, final String newName) {
        execute(session -> {
            session.move(node.getPath(), node.getParent().getPath() + "/" + newName);
            return null;
        });
    }

    public boolean isLive() {
        return execute(Session::isLive);
    }

    public boolean itemExists(final String absPath) {
        return execute(session -> session.itemExists(absPath));
    }

    public void move(final String srcAbsPath, final String destAbsPath) {
        execute(session -> {
            session.move(srcAbsPath, destAbsPath);
            return null;
        });
    }

    public void save() {
        execute(session -> {
            session.save();
            return null;
        });
    }

    public String dump(final Node node) {

        return execute(session -> {
            Node nd = node;

            if (nd == null)
                nd = session.getRootNode();

            return dumpNode(nd);
        });

    }

    /**
     * Recursive method for dumping a node. This method is separate to avoid the
     * overhead of searching and opening/closing JCR sessions.
     *
     * @param node node to dump
     * @return string representation
     * @throws RepositoryException if error
     */
    protected String dumpNode(Node node) throws RepositoryException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(node.getPath());

        PropertyIterator properties = node.getProperties();
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            buffer.append(property.getPath()).append("=");
            if (property.getDefinition().isMultiple()) {
                Value[] values = property.getValues();
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        buffer.append(",");
                    }
                    buffer.append(values[i].getString());
                }
            }
            else {
                buffer.append(property.getString());
            }
            buffer.append("\n");
        }

        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            Node child = nodes.nextNode();
            buffer.append(dumpNode(child));
        }
        return buffer.toString();
    }

    public QueryResult query(final Node node) {

        if (node == null)
            throw new IllegalArgumentException("node can't be null");

        return execute(session -> {
            boolean debug = log.isDebugEnabled();

            // get query manager
            QueryManager manager = session.getWorkspace().getQueryManager();
            if (debug)
                log.debug("retrieved manager " + manager);

            Query query = manager.getQuery(node);
            if (debug)
                log.debug("created query " + query);

            return query.execute();
        });
    }

    public QueryResult query(final String statement) {
        return query(statement, null);
    }


    public QueryResult query(final String statement, String language) {

        if (statement == null)
            throw new IllegalArgumentException("statement can't be null");

        return execute(session -> {
            // check language
            String lang = language;
            if (lang == null)
                lang = Query.XPATH;
            boolean debug = log.isDebugEnabled();

            // get query manager
            QueryManager manager = session.getWorkspace().getQueryManager();
            if (debug)
                log.debug("retrieved manager " + manager);

            Query query = manager.createQuery(statement, lang);
            if (debug)
                log.debug("created query " + query);

            return query.execute();
        });
    }


    public boolean isVersionable(Node node) throws RepositoryException {
        return node.isNodeType("mix:versionable");
    }

    public Node putVersionableFile(
            @NotNull Node parent, @NotNull String name, @NotNull String mime,
            @NotNull InputStream data, @NotNull Calendar date) throws RepositoryException {

        Binary binary = parent.getSession().getValueFactory().createBinary(data);
        VersionManager manager = parent.getSession().getWorkspace().getVersionManager();
        Node file = null;
        boolean existing = JcrUtils.getNodeIfExists(parent, name) != null;

        try {
            file = JcrUtils.getOrAddNode(parent, name, NodeType.NT_FILE);
            file.addMixin("mix:versionable");
            Node content = JcrUtils.getOrAddNode(file, Node.JCR_CONTENT, NodeType.NT_RESOURCE);
            content.addMixin("mix:versionable");

            if (existing) {
                log.debug("Checking in file {}", file.getPath());
                manager.checkin(file.getPath());
            } else {
                log.debug("Create new file {}", name);
            }

            return getNode(mime, date, binary, file, content);
        } finally {
            binary.dispose();
            if(file != null && !file.isCheckedOut()) {
                log.debug("Checking out file {}", file.getPath());
                manager.checkout(file.getPath());
            }
        }
    }

    public Node putFile(
            @NotNull Node parent, @NotNull String name, @NotNull String mime,
            @NotNull InputStream data, @NotNull Calendar date) throws RepositoryException {

        Binary binary = parent.getSession().getValueFactory().createBinary(data);
        try {
            Node file = JcrUtils.getOrAddNode(parent, name, NodeType.NT_FILE);
            file.addMixin("mix:versionable");
            Node content = JcrUtils.getOrAddNode(file, Node.JCR_CONTENT, NodeType.NT_RESOURCE);
            content.addMixin("mix:versionable");

            return getNode(mime, date, binary, file, content);
        } finally {
            binary.dispose();
        }
    }

    private Node getNode(@NotNull String mime, @NotNull Calendar date, Binary binary, Node file, Node content) throws RepositoryException {
        content.setProperty(Property.JCR_MIMETYPE, mime);
        String[] parameters = mime.split(";");
        for (int i = 1; i < parameters.length; i++) {
            int equals = parameters[i].indexOf('=');
            if (equals != -1) {
                String parameter = parameters[i].substring(0, equals);
                if ("charset".equalsIgnoreCase(parameter.trim())) {
                    content.setProperty(
                            Property.JCR_ENCODING,
                            parameters[i].substring(equals + 1).trim());
                }
            }
        }

        content.setProperty(Property.JCR_LAST_MODIFIED, date);
        content.setProperty(Property.JCR_DATA, binary);
        return file;
    }

    public void checkin(@NotNull String path) {
        execute(session -> {
            VersionManager manager = session.getWorkspace().getVersionManager();
            manager.checkin(path);
            return null;
        });
    }

    public void checkout(@NotNull String path) {
        execute(session -> {
            VersionManager manager = session.getWorkspace().getVersionManager();
            manager.checkout(path);
            return null;
        });
    }

    public Stream<Version> getVersions(@NotNull String path) {
        return execute(session -> {
            VersionManager manager = session.getWorkspace().getVersionManager();
            VersionHistory history = manager.getVersionHistory(path);

            VersionIterator it = history.getAllVersions();

            boolean debug = log.isDebugEnabled();

            while (it.hasNext()) {
                Version v = it.nextVersion();
                if (debug) log.debug("v.getCreated().getTime()");
            }

            @SuppressWarnings("unchecked")
            Spliterator<Version> spliterator = Spliterators.spliteratorUnknownSize(history.getAllVersions(), 0);
            return StreamSupport.stream(spliterator, false);
        });
    }

    public InputStream readFile(Node node) throws RepositoryException {
        return JcrUtils.readFile(node);
    }

    public void readFile(Node node, OutputStream output) throws RepositoryException, IOException {
        JcrUtils.readFile(node, output);
    }

}




