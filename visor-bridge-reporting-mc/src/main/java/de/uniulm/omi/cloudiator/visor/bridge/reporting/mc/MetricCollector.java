/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.visor.bridge.reporting.mc;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.paasage.Constants;
import de.uniulm.omi.cloudiator.paasage.MetricQueue;
import de.uniulm.omi.cloudiator.visor.bridge.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.bridge.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.bridge.reporting.ReportingInterface;
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

    @Inject public MetricCollector(@Named("mcResourceName") String resourceName, @Named("mcModelName") String modelName) {
        checkNotNull(modelName);
        checkArgument(!modelName.isEmpty(), "Model name must not be empty.");
        checkNotNull(resourceName);
        checkArgument(!resourceName.isEmpty(), "Resource name must not be empty.");
        
        //TODO start metriccollector as singleton, static variable MetricCollectorRunnable
        this.metricQueue = initQueue();
    }

    private MetricQueue initQueue() {
        MetricQueue mq = null;
        try{
            Registry reg = LocateRegistry.getRegistry(Constants.LOCALHOST_IP);
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
     * @throws de.uniulm.omi.cloudiator.visor.bridge.reporting.ReportingException If the CDO server could not be reached.
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
