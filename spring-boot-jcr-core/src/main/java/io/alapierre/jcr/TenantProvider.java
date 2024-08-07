package io.alapierre.jcr;

import java.util.Optional;

/**
 * Created 03.09.2021
 *
 * @author Karol Bryzgiel {@literal <karol.bryzgiel@soft-project.pl>}
 */
@FunctionalInterface
public interface TenantProvider {

    Optional<String> getTenant();
}
