/*
 * Copyright (C) 2015 University of Ulm.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

package org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.kairos;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.config.ConfigurationException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.monitoring.Metric;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException;
import org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A concrete implementation of the reporting interface, reporting
 * metrics to the kairos database.
 */
public class KairosDb implements ReportingInterface<Metric> {

    /**
     * The server of the kairos db.
     */
    protected final String server;
    /**
     * The port of the kairos db.
     */
    protected final int port;

    /**
     * A logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(KairosDb.class);

    @Inject public KairosDb(@Named("kairosServer") String server, @Named("kairosPort") int port) {
        checkNotNull(server);
        checkArgument(!server.isEmpty(), "Server must not be empty.");
        checkArgument(port > 0, "Port must be >0");
        this.server = server;
        this.port = port;
    }

    /**
     * Sends a metric to the kairos server.
     *
     * @param metricBuilder the metricbuilder containing the metrics.
     * @throws org.ow2.paasage.camel.srl.colosseum_mca_bridge.reporting.ReportingException If the kairos server could not be reached.
     */
    protected void sendMetric(MetricBuilder metricBuilder) throws ReportingException {
        try {
            HttpClient client = new HttpClient("http://" + this.server + ":" + this.port);
            Response response = client.pushMetrics(metricBuilder);

            //check if response is ok
            if (response.getStatusCode() / 100 != 2) {
                LOGGER.error("Kairos DB reported error. Status code: " + response.getStatusCode());
                LOGGER.error("Error message: " + response.getErrors());
                throw new ReportingException();
            } else {
                LOGGER.debug("Kairos DB returned OK. Status code: " + response.getStatusCode());
            }
            client.shutdown();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new ConfigurationException(e);
        } catch (IOException e) {
            LOGGER.error("Could not request KairosDB.", e);
            throw new ReportingException(e);
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
        KairosMetricConverter kairosMetricConverter = new KairosMetricConverter();
        try {
            kairosMetricConverter.add(metric);
        } catch (KairosMetricConversionException e) {
            throw new ReportingException(e);
        }
        this.sendMetric(kairosMetricConverter.convert());
    }

    /**
     * Report method for multiple metrics.
     * Converts the metrics to metrics kairos can understand and sends them.
     *
     * @param metrics a collection of metrics.
     * @throws ReportingException if the metric could not be converted or sent to kairos.
     */
    @Override public void report(Collection<Metric> metrics) throws ReportingException {
        KairosMetricConverter kairosMetricConverter = new KairosMetricConverter();
        for (Metric metric : metrics) {
            LOGGER.debug(String.format("Reporting new metric: %s", metric));
            try {
                kairosMetricConverter.add(metric);
            } catch (KairosMetricConversionException e) {
                throw new ReportingException(e);
            }
        }
        this.sendMetric(kairosMetricConverter.convert());
    }

}
