package io.alapierre.jcr.auto;

import io.alapierre.jcr.JcrSessionFactory;
import io.alapierre.jcr.JcrTemplate;
import io.alapierre.jcr.SessionFactory;
import io.alapierre.jcr.TenantProvider;
import io.alapierre.jcr.auto.config.JackrabbitRepositoryConfigFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import java.util.Optional;

/**
 * Created 13.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@Configuration
@ConditionalOnClass(JcrTemplate.class)
@EnableConfigurationProperties(JcrProperties.class)
@Slf4j
@RequiredArgsConstructor
public class JcrAutoConfigurer {

    private final JcrProperties config;

    @Bean
    public JackrabbitRepositoryConfigFactory jcrConfigFactory() {
        return new JackrabbitRepositoryConfigFactory(config);
    }

    @Bean
    public RepositoryConfig jcrConfig(JackrabbitRepositoryConfigFactory cf) {
        try {
            return cf.create();
        } catch (Exception e) {
            log.error("Cant create repository config", e);
            throw new IllegalStateException(e);
        }
    }

    @Bean
    public JackrabbitRepository jcrRepository(RepositoryConfig rc) throws RepositoryException {
        return RepositoryImpl.create(rc);
    }

    @Bean
    public SessionFactory jcrSessionFactory(Repository repository) {
        Credentials credentials = new SimpleCredentials(config.getUserName(), config.getPassword().toCharArray());
        return new JcrSessionFactory(repository, null, credentials);
    }

    @Bean
    public JcrTemplate jcrTemplate(SessionFactory sessionFactory, ObjectProvider<TenantProvider> tenantProvider) {
        TenantProvider provider = tenantProvider.getIfAvailable();
        if( provider != null) {
            log.info("JCR Template initializing with tenantProvider");
            return new JcrTemplate(sessionFactory, provider);
        } else {
            log.info("JCR Template initializing without tenantProvider");
            return new JcrTemplate(sessionFactory, Optional::empty);
        }
    }

}
