package de.uniulm.omi.cloudiator.visor.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.util.Set;

/**
 * Created by Frank on 17.08.2015.
 *
 * TODO added here because of lack of extensibility.
 */
public class CdoModule extends AbstractModule {

    @Override protected void configure() {

    }

    @Provides @Named("mcModelName") public String provideModelName(Set<McModelNameProvider> providers) {
        for (McModelNameProvider provider : providers) {
            if (provider.getModelName() != null) {
                return provider.getModelName();
            }
        }
        throw new ConfigurationException("Could not resolve the model name.");
    }

    @Provides @Named("mcResourceName") public String provideResourceName(Set<McResourceNameProvider> providers) {
        for (McResourceNameProvider provider : providers) {
            if (provider.getResourceName() != null) {
                return provider.getResourceName();
            }
        }
        throw new ConfigurationException("Could not resolve the model name.");
    }
}
