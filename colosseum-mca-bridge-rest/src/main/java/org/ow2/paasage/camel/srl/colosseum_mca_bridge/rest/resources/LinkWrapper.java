/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Set;

/**
 * Created by daniel on 07.04.15.
 */
public class LinkWrapper<T> {

    protected T wrappedEntity;
    private final Set<Link> links;

    public LinkWrapper(T wrappedEntity, Set<Link> links) {
        this.wrappedEntity = wrappedEntity;
        this.links = links;
    }

    @JsonUnwrapped public T getEntity() {
        return wrappedEntity;
    }

    public Set<Link> getLinks() {
        return links;
    }

}
