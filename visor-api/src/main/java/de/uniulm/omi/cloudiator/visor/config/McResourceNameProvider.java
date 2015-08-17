package de.uniulm.omi.cloudiator.visor.config;

import javax.annotation.Nullable;

/**
 * Created by Frank on 17.08.2015.
 */
public interface McResourceNameProvider {

    @Nullable public String getResourceName();
}
