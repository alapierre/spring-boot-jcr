package io.alapierre.jcr.auto.config;

import io.alapierre.jcr.auto.JcrProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.RepositoryConfigurationParser;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RequiredArgsConstructor
@Slf4j
public class JackrabbitRepositoryConfigFactory {

    private final JcrProperties config;

    @Getter
    @Setter
    private Resource repositoryConfig = new ClassPathResource("repository.xml");

    /**
     * Creates a JackRabbit RepositoryConfig.
     *
     * @return RepositoryConfig
     * @throws IOException when can't loaf repository config file
     * @throws BeanCreationException when can't create RepositoryConfig
     */
    public RepositoryConfig create() throws IOException, BeanCreationException {
        Properties properties = new Properties();
        properties.setProperty(RepositoryConfigurationParser.REPOSITORY_HOME_VARIABLE, config.getRepositoryHome());

        try {
            InputStream is = repositoryConfig.getInputStream();
            return RepositoryConfig.create(new InputSource(is), properties);
        } catch(ConfigurationException e){
            throw new BeanCreationException("Unable to configure repository with: " + repositoryConfig + " and " + properties);
        }
    }
}