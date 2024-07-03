package io.alapierre.jcr.auto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

/**
 * Created 14.02.2020 copyright original authors 2020
 *
 * @author Adrian Lapierre {@literal <al@soft-project.pl>}
 */
@ConfigurationProperties(prefix = "sp.jcr")
@Data
@Validated
public class JcrProperties {

    @NotEmpty
    private String userName;

    @NotEmpty
    private String password;

    @Value("jackrabbit")
    private String repositoryHome;

    private Resource repositoryConfig = new ClassPathResource("repository.xml");

    @NotEmpty
    private String datasourceUser;

    @NotEmpty
    private String datasourcePassword;

    @NotEmpty
    private String datasourceUrl;

}
