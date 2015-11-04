/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.execution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.*;

/**
 * Created by daniel on 11.12.14.
 */
@Singleton public class DefaultScheduledExecutionService implements ScheduledExecutionService {

    private static final Logger LOGGER = LogManager.getLogger(ScheduledExecutionService.class);

    /**
     * The executor service used for scheduling the probe runs.
     */
    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<Schedulable, ScheduledFuture> registeredSchedulables;

    @Inject
    public DefaultScheduledExecutionService(@Named("executionThreads") int executionThreads) {
        checkArgument(executionThreads >= 1, "Execution thread must be >= 1");
        LOGGER.debug(String.format("Starting execution service with %s threads", executionThreads));
        scheduledExecutorService = ExtendedScheduledThreadPoolExecutor.create(executionThreads);
        registeredSchedulables = new HashMap<>();
    }

    @Override public void schedule(Schedulable schedulable) {
        checkNotNull(schedulable);
        LOGGER.debug(
            "Scheduling " + schedulable.getClass().getName() + " with interval of " + schedulable
                .getInterval());
        final ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService
            .scheduleAtFixedRate(schedulable.getRunnable(), 0,
                schedulable.getInterval().getPeriod(), schedulable.getInterval().getTimeUnit());
        this.registeredSchedulables.put(schedulable, scheduledFuture);
    }

    @Override public void remove(Schedulable schedulable) {
        checkNotNull(schedulable);
        checkState(this.registeredSchedulables.containsKey(schedulable),
            "The schedulable " + schedulable + " was never registered with the scheduler.");
        this.registeredSchedulables.get(schedulable).cancel(false);
        this.registeredSchedulables.remove(schedulable);
    }

    @Override public void reschedule(Schedulable schedulable) {
        checkNotNull(schedulable);
        this.remove(schedulable);
        this.schedule(schedulable);
    }

    @Override public void execute(Runnable runnable) {
        checkNotNull(runnable);
        this.scheduledExecutorService.execute(runnable);
    }

    @Override public void shutdown(final int seconds) {

        LOGGER.debug(String.format("Shutting down execution service in %d seconds", seconds));

        try {
            // Wait a while for existing tasks to terminate
            if (!this.scheduledExecutorService.awaitTermination(seconds, TimeUnit.SECONDS)) {
                this.scheduledExecutorService.shutdownNow();
                if (!this.scheduledExecutorService.awaitTermination(seconds, TimeUnit.SECONDS))
                    LOGGER.error("Execution pool did not terminate.");
            }
        } catch (InterruptedException ie) {
            this.scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override public void kill() {
        this.scheduledExecutorService.shutdownNow();
    }
}
