[![Renovate enabled](https://img.shields.io/badge/renovate-enabled-brightgreen.svg)](https://renovatebot.com/)

# spring-boot-jcr
Java Content Repository SpringBoot integration 

based on https://github.com/astubbs/spring-modules/

Limitations:

- thread bound session is not supported yet

## Usage

Just add 

```xml
<dependency>        
    <groupId>io.alapierre</groupId>
    <artifactId>spring-boot-jcr-autoconfigurer</artifactId>
    <version>1.4</version> 
</dependency>
```

### Configuration

application.properties

```
sp.jcr.user-name=
sp.jcr.password=
sp.jcr.datasource-user=
sp.jcr.datasource-password=
sp.jcr.datasource-url=
```

example using variables in repository.xml

```xml
        <PersistenceManager class="org.apache.jackrabbit.core.persistence.pool.PostgreSQLPersistenceManager">
            <param name="driver" value="org.postgresql.Driver"/>
            <param name="url" value="${sp.jcr.datasource-url}"/>
            <param name="schema" value="postgresql"/> <!-- ddl file name, not database schema name -->
            <param name="user" value="${sp.jcr.datasource-user}"/>
            <param name="password" value="${sp.jcr.datasource-password}"/>
            <param name="schemaObjectPrefix" value="version_"/>
            <param name="externalBLOBs" value="false"/>
        </PersistenceManager>
```

repository.xml

```xml
<?xml version="1.0"?>
<!DOCTYPE Repository
        PUBLIC "-//The Apache Software Foundation//DTD Jackrabbit 2.0//EN"
        "http://jackrabbit.apache.org/dtd/repository-2.0.dtd">

<Repository>
    <!--
        virtual file system where the repository stores global state
        (e.g. registered namespaces, custom node types, etc.)
    -->
    <FileSystem class="org.apache.jackrabbit.core.fs.local.LocalFileSystem">
        <param name="path" value="${rep.home}/repository"/>
    </FileSystem>

    <!--
        data store configuration
    -->
    <DataStore class="org.apache.jackrabbit.core.data.FileDataStore"/>

    <!--
        security configuration
    -->
    <Security appName="Jackrabbit">
        <!--
            security manager:
            class: FQN of class implementing the JackrabbitSecurityManager interface
        -->
        <SecurityManager class="org.apache.jackrabbit.core.DefaultSecurityManager" workspaceName="security">
            <!--
            workspace access:
            class: FQN of class implementing the WorkspaceAccessManager interface
            -->
            <!-- <WorkspaceAccessManager class="..."/> -->
            <!-- <param name="config" value="${rep.home}/security.xml"/> -->
        </SecurityManager>

        <!--
            access manager:
            class: FQN of class implementing the AccessManager interface
        -->
        <AccessManager class="org.apache.jackrabbit.core.security.DefaultAccessManager">
            <!-- <param name="config" value="${rep.home}/access.xml"/> -->
        </AccessManager>

        <LoginModule class="org.apache.jackrabbit.core.security.simple.SimpleLoginModule">
            <!--
               anonymous user name ('anonymous' is the default value)
             -->
            <param name="anonymousId" value="anonymous"/>
            <!--
               administrator user id (default value if param is missing is 'admin')
             -->
            <param name="adminId" value="admin"/>

            <param name="admin" value="admin"/>

        </LoginModule>
    </Security>

    <!--
        location of workspaces root directory and name of default workspace
    -->
    <Workspaces rootPath="${rep.home}/workspaces" defaultWorkspace="default"/>
    <!--
        workspace configuration template:
        used to create the initial workspace if there's no workspace yet
    -->
    <Workspace name="${wsp.name}">
        <!--
            virtual file system of the workspace:
            class: FQN of class implementing the FileSystem interface
        -->
        <FileSystem class="org.apache.jackrabbit.core.fs.local.LocalFileSystem">
            <param name="path" value="${wsp.home}"/>
        </FileSystem>
        <!--
            persistence manager of the workspace:
            class: FQN of class implementing the PersistenceManager interface
        -->
        <PersistenceManager class="org.apache.jackrabbit.core.persistence.pool.DerbyPersistenceManager">
            <param name="url" value="jdbc:derby:${wsp.home}/db;create=true"/>
            <param name="schemaObjectPrefix" value="${wsp.name}_"/>
        </PersistenceManager>
        <!--
            Search index and the file system it uses.
            class: FQN of class implementing the QueryHandler interface
        -->
        <SearchIndex class="org.apache.jackrabbit.core.query.lucene.SearchIndex">
            <param name="path" value="${wsp.home}/index"/>
            <param name="supportHighlighting" value="true"/>
        </SearchIndex>
    </Workspace>

    <!--
        Configures the versioning
    -->
    <Versioning rootPath="${rep.home}/version">
        <!--
            Configures the filesystem to use for versioning for the respective
            persistence manager
        -->
        <FileSystem class="org.apache.jackrabbit.core.fs.local.LocalFileSystem">
            <param name="path" value="${rep.home}/version" />
        </FileSystem>

        <!--
            Configures the persistence manager to be used for persisting version state.
            Please note that the current versioning implementation is based on
            a 'normal' persistence manager, but this could change in future
            implementations.
        -->
        <PersistenceManager class="org.apache.jackrabbit.core.persistence.pool.DerbyPersistenceManager">
            <param name="url" value="jdbc:derby:${rep.home}/version/db;create=true"/>
            <param name="schemaObjectPrefix" value="version_"/>
        </PersistenceManager>
    </Versioning>

    <!--
        Search index for content that is shared repository wide
        (/jcr:system tree, contains mainly versions)
    -->
    <SearchIndex class="org.apache.jackrabbit.core.query.lucene.SearchIndex">
        <param name="path" value="${rep.home}/repository/index"/>
        <param name="supportHighlighting" value="true"/>
    </SearchIndex>

    <!--
        Run with a cluster journal
    -->
    <Cluster id="node1">
        <Journal class="org.apache.jackrabbit.core.journal.MemoryJournal"/>
    </Cluster>
</Repository>
```
