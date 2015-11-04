/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge;

import org.apache.commons.cli.ParseException;


public class Visor {

    public static void main(final String[] args) throws ParseException {
        VisorServiceBuilder.create().args(args).build().start();
    }

}
