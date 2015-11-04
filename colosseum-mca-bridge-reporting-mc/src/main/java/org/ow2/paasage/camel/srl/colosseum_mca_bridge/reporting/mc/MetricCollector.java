/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.mc;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.metrics_collector_accessor.Constants;
import org.ow2.paasage.camel.srl.metrics_collector_accessor.MetricQueue;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A concrete implementation of the reporting interface, reporting
 * metrics to the kairos database.
 */
public class MetricCollector implements ReportingInterface<Metric> {

    private final MetricQueue metricQueue;

    /**
     * A logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetricCollector.class);

    public MetricCollector() {
        
        //TODO start metriccollector as singleton, static variable MetricCollectorRunnable
        this.metricQueue = initQueue();
    }

    private MetricQueue initQueue() {
        MetricQueue mq = null;
        try{
            Registry reg = LocateRegistry.getRegistry(Constants.LOCALHOST_IP, Registry.REGISTRY_PORT + 10);
            Object o = reg.lookup(Constants.QUEUE_REGISTRY_KEY);
            mq = (MetricQueue) o;
            return mq;
        } catch (RemoteException rex){
            LOGGER.error("Could not connect to remote object. (1)");
        } catch (NotBoundException nbex){
            LOGGER.error("Could not connect to remote object. (2)");
        } catch (Exception ex){
            LOGGER.error("Could not connect to remote object. (3)");
        }
        throw new IllegalStateException();
    }

    /**
     * Sends a metric to the cdo and zeromq server.
     *
     * //@param metricBuilder the metricbuilder containing the metrics.
     * @throws org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException If the CDO server could not be reached.
     */
    protected void sendMetric(MetricCollectorConversion con) throws ReportingException {
        if(!con.isEvent()){

            LOGGER.debug("Send measurement request to RMI server.");

            if(this.metricQueue != null){
                try{
                    this.metricQueue.addMeasurement(con.getId(), con.getValue());
                } catch (RemoteException rex){
                    rex.printStackTrace();
                    LOGGER.error("could not call measurement method");
                }
            }
        } else {

            LOGGER.debug("Send event request to RMI server.");

            if(this.metricQueue != null){
                try{
                    this.metricQueue.addEvent(con.getId(), con.getValue());
                } catch (RemoteException rex){
                    rex.printStackTrace();
                    LOGGER.error("could not call event method");
                }
            }
        }
    }

    /**
     * Report method.
     * Converts the given metric to a metric kairos understands and sends them.
     *
     * @param metric the metric to report.
     * @throws ReportingException of the metric could not be converted. or sent to kairos.
     */
    @Override public void report(Metric metric) throws ReportingException {
        LOGGER.debug(String.format("Reporting new metric: %s", metric));
        try {
            this.sendMetric(new MetricCollectorConversion(metric));
        } catch (McMetricConversionException e) {
            throw new ReportingException(e);
        }
    }

    /**
     * Report method for multiple metrics.
     * Converts the metrics to metrics kairos can understand and sends them.
     *
     * @param metrics a collection of metrics.
     * @throws ReportingException if the metric could not be converted or sent to kairos.
     */
    @Override public void report(Collection<Metric> metrics) throws ReportingException {
        for (Metric metric : metrics) {
            LOGGER.debug(String.format("Reporting new metric: %s", metric));
            try {
                this.sendMetric(new MetricCollectorConversion(metric));
            } catch (McMetricConversionException e) {
                throw new ReportingException(e);
            }
        }
    }

}
