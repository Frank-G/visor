/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

/**
 * Created by daniel on 25.03.15.
 */
public class ExtendedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public static ScheduledExecutorService create(int nThreads) {
        return new ExtendedScheduledThreadPoolExecutor(nThreads);
    }

    private static final Logger LOGGER =
        LogManager.getLogger(ExtendedScheduledThreadPoolExecutor.class);

    public ExtendedScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public ExtendedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public ExtendedScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public ExtendedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
        RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                if (((Future) r).isDone() && !((Future) r).isCancelled()) {
                    ((Future) r).get();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                t = e.getCause();
            }
        }
        if (t != null) {
            LOGGER.fatal("Uncaught exception occurred during the execution of task.", t);
        }
    }
}
