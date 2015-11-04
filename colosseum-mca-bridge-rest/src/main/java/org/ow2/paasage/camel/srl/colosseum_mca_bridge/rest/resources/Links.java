/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.rest.resources;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by daniel on 07.04.15.
 */
public class Links {

    private Links() {

    }

    public static Set<Link> selfLink(String href) {
        Link link = new Link(href,Rel.SELF);
        HashSet<Link> links = new HashSet<>();
        links.add(link);
        return links;
    }
}
