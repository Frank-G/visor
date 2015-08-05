package de.uniulm.omi.cloudiator.visor.reporting.mc;

import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import eu.paasage.camel.Application;
import eu.paasage.camel.CamelModel;
import eu.paasage.camel.execution.ExecutionContext;
import eu.paasage.camel.metric.*;
import eu.paasage.executionware.metric_collector.MetricStorage;
import eu.paasage.mddb.cdo.client.CDOClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 03.08.2015.
 */
public class MetricCollectorCache {

    /**
     * A logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetricCollectorCache.class);

    private static MetricCollectorCache singleton;

    private Map<String, MeasurementParameters> cache = new HashMap<String, MeasurementParameters>(); // <monitorinstance_id, params>
    private final String modelName;
    private final CdoClientWrapper cdo;

    private MetricCollectorCache(String modelName){
        this.modelName = modelName;
        this.cdo = new CdoClientWrapper(new CDOClient(), modelName); /* TODO config variables in file*/
    }

    public static MetricCollectorCache create(String modelName){
        if(singleton == null){
            singleton = new MetricCollectorCache(modelName);
        }
        return singleton;
    }

    public MeasurementParameters getParameters(String id){
        for(Map.Entry<String, MeasurementParameters> s : cache.entrySet()){
            if(s.getKey().equals(id)){
                return s.getValue();
            }
        }

        // if not returned, ip not found in set
        MetricStorage.MeasurementType type = cdo.getMeasurementType(id);

        MeasurementParameters params = new MeasurementParameters(
            cdo.getObjectOfMetricInstance(id), // CDOID metricInstanceID,
            cdo.getObjectOfExecutionContext(id), // CDOID execContextInstanceID,
            type, // MetricStorage.MeasurementType measurementType,
            cdo.getMeasurementObject(type, id), // CDOID measurementObject1,
            null //TODO not used currently: CDOID measurementObject2
        );
        cache.put(id, params);
        return params;
    }

    public void refresh(){
        for(Map.Entry<String, MeasurementParameters> p : cache.entrySet()){
            //TODO
        }
    }

    public String getModelName() {
        return modelName;
    }

    public class CdoClientWrapper{
        private final eu.paasage.mddb.cdo.client.CDOClient cdoClient;
        private final String modelName;
        private CDOView view;

        public CdoClientWrapper(CDOClient cdoClient, String modelName) {
            this.cdoClient = cdoClient;
            this.modelName = modelName;

            view = this.cdoClient.openView();
        }

        public CDOID getObjectOfMetricInstance(String metricInstance){
            MetricInstance mi = this.getMetricInstance(metricInstance);
            return (mi == null ? null : mi.cdoID());
        }

        public CDOID getObjectOfExecutionContext(String metricInstance){
            MetricInstance mi = this.getMetricInstance(metricInstance);
            return (mi == null ? null : mi.getObjectBinding().getExecutionContext().cdoID());
        }

        public MetricStorage.MeasurementType getMeasurementType(String metricInstance){
            MetricInstance mi = this.getMetricInstance(metricInstance);
            if (mi == null){
                return null; //TODO exception?
            }

            MetricObjectBinding mob = mi.getObjectBinding();

            if(mob instanceof MetricVMBinding){
                return MetricStorage.MeasurementType.RESOURCE_MEASUREMENT;
            } else if(mob instanceof MetricComponentBinding){
                return MetricStorage.MeasurementType.COMPONENT_MEASUREMENT;
            } else if(mob instanceof MetricApplicationBinding){
                return MetricStorage.MeasurementType.APPLICATION_MEASUREMENT;
            } else {
                return null; // TODO other types?
            }
        }

        public CDOID getMeasurementObject(MetricStorage.MeasurementType type, String metricInstance){
            MetricInstance mi = this.getMetricInstance(metricInstance);
            if (mi == null){
                return null; //TODO exception?
            }

            MetricObjectBinding mob = mi.getObjectBinding();

            switch (type){
                case RESOURCE_MEASUREMENT : return ((MetricVMBinding)mob).getVmInstance().cdoID();
                case COMPONENT_MEASUREMENT : return ((MetricComponentBinding)mob).getComponentInstance().cdoID();
                case APPLICATION_MEASUREMENT : return mob.getExecutionContext().getApplication().cdoID() /*TODO why no reference?*/;
            }

            return null; //TODO other types?
        }

        //TODO cache result
        public MetricInstance getMetricInstance(String metricInstance){
            EList<EObject> objs = view.getResource(modelName).getContents();
            for(EObject obj : objs) {
                LOGGER.info("The objs stored are: " + obj.toString());

                // Get Couchbase Camel Model
                CamelModel model = (CamelModel) obj;

                for(MetricInstance mi : model.getMetricModels().get(0).getMetricInstances()){
                    if(mi.getName().equals(metricInstance)){
                        return mi;
                    }
                }
            }

            return null; //TODO exception?
        }

        protected void finalize(){
            view.close();
        }
    }

    public class MeasurementParameters {
        private final org.eclipse.emf.cdo.common.id.CDOID metricInstanceID;
        private final org.eclipse.emf.cdo.common.id.CDOID execContextInstanceID;
        private final MetricStorage.MeasurementType measurementType;
        private final org.eclipse.emf.cdo.common.id.CDOID measurementObject1;
        private final org.eclipse.emf.cdo.common.id.CDOID measurementObject2;

        public MeasurementParameters(CDOID metricInstanceID, CDOID execContextInstanceID,
            MetricStorage.MeasurementType measurementType, CDOID measurementObject1,
            CDOID measurementObject2) {
            this.metricInstanceID = metricInstanceID;
            this.execContextInstanceID = execContextInstanceID;
            this.measurementType = measurementType;
            this.measurementObject1 = measurementObject1;
            this.measurementObject2 = measurementObject2;
        }

        public CDOID getMetricInstanceID() {
            return metricInstanceID;
        }

        public CDOID getExecContextInstanceID() {
            return execContextInstanceID;
        }

        public MetricStorage.MeasurementType getMeasurementType() {
            return measurementType;
        }

        public CDOID getMeasurementObject1() {
            return measurementObject1;
        }

        public CDOID getMeasurementObject2() {
            return measurementObject2;
        }
    }
}
