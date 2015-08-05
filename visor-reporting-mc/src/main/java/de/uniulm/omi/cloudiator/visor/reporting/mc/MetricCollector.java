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

package de.uniulm.omi.cloudiator.visor.reporting.mc;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniulm.omi.cloudiator.visor.config.ConfigurationException;
import de.uniulm.omi.cloudiator.visor.monitoring.Metric;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingException;
import de.uniulm.omi.cloudiator.visor.reporting.ReportingInterface;
import eu.paasage.executionware.metric_collector.CDOListener;
import eu.paasage.executionware.metric_collector.MetricStorage;
import eu.paasage.executionware.metric_collector.pubsub.PublicationServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.cdo.common.id.CDOID;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A concrete implementation of the reporting interface, reporting
 * metrics to the kairos database.
 */
public class MetricCollector implements ReportingInterface<Metric> {

    private final PublicationServer pubServer;
    private static final int CORE_POOL_SIZE = 10, MAX_POOL_SIZE = 20, ALIVE_TIME = 100;
    private ThreadPoolExecutor tpe;
    private final HashSet<CDOID> pubIds = new HashSet<>();
    private final MetricCollectorCache cache;

    /**
     * A logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetricCollector.class);

    @Inject public MetricCollector(@Named("mcModelName") String modelName) {
        checkNotNull(modelName);
        checkArgument(!modelName.isEmpty(), "Model name must not be empty.");
        this.tpe = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,ALIVE_TIME, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(CORE_POOL_SIZE));
        
        //TODO start metriccollector as singleton, static variable MetricCollectorRunnable
        this.pubServer = new PublicationServer();
        this.cache = MetricCollectorCache.create(modelName);
    }

    /**
     * Sends a metric to the cdo and zeromq server.
     *
     * //@param metricBuilder the metricbuilder containing the metrics.
     * @throws de.uniulm.omi.cloudiator.visor.reporting.ReportingException If the CDO server could not be reached.
     */
    protected void sendMetric(MetricCollectorConversion con) throws ReportingException {
        MetricCollectorCache.MeasurementParameters params = cache.getParameters(con.getMetricInstance());

        if(!pubIds.contains(params.getMetricInstanceID())){
            // destroy running threads:
            tpe.shutdownNow();
            //TODO just a workaround, better do not use shutdown before, so we could make tpe final:
            tpe = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,ALIVE_TIME, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(CORE_POOL_SIZE));
            // add new id
            pubIds.add(params.getMetricInstanceID());
            // start again
            tpe.execute(new CDOListener(pubServer, pubIds));
        }

        LOGGER.debug("Send measurement to cdo.");
        MetricStorage.storeMeasurement(con.getValue(), params.getMetricInstanceID(),
            params.getExecContextInstanceID(), params.getMeasurementType(),
            params.getMeasurementObject1(), params.getMeasurementObject2());

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
