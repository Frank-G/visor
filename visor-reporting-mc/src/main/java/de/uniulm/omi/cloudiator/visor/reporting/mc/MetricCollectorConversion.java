package de.uniulm.omi.cloudiator.visor.reporting.mc;

import de.uniulm.omi.cloudiator.visor.monitoring.Metric;

/**
 * Created by Frank on 03.08.2015.
 */
public class MetricCollectorConversion {
    private final double value;
    private final String metricInstance;
    private final long timestamp;

    public MetricCollectorConversion(Metric metric) throws McMetricConversionException {
        final int split = metric.getName().indexOf("#");
        if (split == -1) {
            throw new McMetricConversionException("No split char in: " + metric.getName());
        }
        if(metric.getName().substring(0, split - 1).equals("cdo")){
            throw new McMetricConversionException("Wrong metric type: " + metric.getName().substring(split));
        }
        metricInstance = metric.getName().substring(split + 1);

        //TODO do this more safe
        value = Double.parseDouble((String)metric.getValue());
        timestamp = metric.getTimestamp();
    }

    public double getValue() {
        return value;
    }

    public String getMetricInstance() {
        return metricInstance;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
