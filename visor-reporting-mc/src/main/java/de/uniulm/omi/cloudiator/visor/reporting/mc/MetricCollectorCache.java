package de.uniulm.omi.cloudiator.visor.reporting.mc;

import eu.paasage.camel.CamelModel;
import eu.paasage.camel.LayerType;
import eu.paasage.camel.execution.Measurement;
import eu.paasage.camel.metric.*;
import eu.paasage.camel.scalability.*;
import eu.paasage.executionware.metric_collector.MetricStorage;
import eu.paasage.mddb.cdo.client.CDOClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private Map<String, MeasurementParameters> cacheMetrics = new HashMap<String, MeasurementParameters>(); // <monitorinstance_id, params>
    private Map<String, EventParameters> cacheEvents = new HashMap<String, EventParameters>(); // <monitorinstance_id, params>
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

    public MeasurementParameters getMeasurementParameters(String id){
        for(Map.Entry<String, MeasurementParameters> s : cacheMetrics.entrySet()){
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
        cacheMetrics.put(id, params);
        return params;
    }

    public EventParameters getEventParameters(String id){
        for(Map.Entry<String, EventParameters> s : cacheEvents.entrySet()){
            if(s.getKey().equals(id)){
                return s.getValue();
            }
        }

        StatusType status = StatusType.SUCCESS; //TODO how to handle status?
        CDOID eventID = cdo.getObjectOfEvent(id);
        CDOID measID = cdo.getRandomMeasurementToOneOfTheMetrics(id); // TODO what if no measurement is linked to an event?
        LayerType layer  = LayerType.IAA_S; // TODO calculate from most highest of all Metrics?!

        EventParameters params = new EventParameters(
            status,
            eventID,
            measID,
            layer
        );
        cacheEvents.put(id, params);
        return params;
    }

    public void refresh(){
        for(Map.Entry<String, MeasurementParameters> p : cacheMetrics.entrySet()){
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

        public CDOID getObjectOfEvent(String eventName){
            Event mi = this.getEvent(eventName);
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

        public CDOID getRandomMeasurementToOneOfTheMetrics(String name){
            Event ev = this.getEvent(name);

            if(ev instanceof FunctionalEvent){
                FunctionalEvent se = (FunctionalEvent) ev;
            } else if (ev instanceof NonFunctionalEvent){
                NonFunctionalEvent se = (NonFunctionalEvent) ev;


                return getRandomMeasurementToMetricContext(
                    se.getMetricCondition().getMetricContext());

            } else if (ev instanceof BinaryEventPattern){
                BinaryEventPattern se = (BinaryEventPattern) ev;

                //trace down till simple event
                return getRandomMeasurementToOneOfTheMetrics(se.getLeftEvent().getName());
            } else if (ev instanceof UnaryEventPattern){
                UnaryEventPattern se = (UnaryEventPattern) ev;

                //trace down till simple event
                return getRandomMeasurementToOneOfTheMetrics(se.getEvent().getName());
            }

            //TODO exception handling
            return null;
        }

        public CDOID getRandomMeasurementToMetricContext(MetricContext mc){
            EList<EObject> objs = view.getResource(modelName).getContents();


            for(EObject obj : objs) {
                LOGGER.info("Search for metriccontext: " + mc.getName());

                // Get Camel Model
                CamelModel model = (CamelModel) obj;

                for(MetricInstance mi : model.getMetricModels().get(0).getMetricInstances()){
                    if(mi.getMetricContext().getName().equals(mc.getName())){
                        for(Measurement m : model.getExecutionModels().get(0).getMeasurements()){
                            if(m.getMetricInstance().getName().equals(mi.getName())){
                                return mi.cdoID();
                            }
                        }
                    }
                }
            }

            return null; //TODO exception?
        }

        //TODO cache result
        public MetricInstance getMetricInstance(String metricInstance){
            EList<EObject> objs = view.getResource(modelName).getContents();
            for(EObject obj : objs) {
                LOGGER.info("The objs stored are: " + obj.toString());

                // Get Camel Model
                CamelModel model = (CamelModel) obj;

                for(MetricInstance mi : model.getMetricModels().get(0).getMetricInstances()){
                    if(mi.getName().equals(metricInstance)){
                        return mi;
                    }
                }
            }

            return null; //TODO exception?
        }

        //TODO cache result
        public Event getEvent(String name){
            EList<EObject> objs = view.getResource(modelName).getContents();
            for(EObject obj : objs) {
                LOGGER.info("The objs stored are: " + obj.toString());

                // Get Camel Model
                CamelModel model = (CamelModel) obj;

                for(Event ev : model.getScalabilityModels().get(0).getEvents()){
                    if(ev.getName().equals(name)){
                        return ev;
                    }
                }

                for(EventPattern ev : model.getScalabilityModels().get(0).getPatterns()){
                    if(ev.getName().equals(name)){
                        return ev;
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

    public class EventParameters {
        private final StatusType status;
        private final CDOID eventID;
        private final CDOID measID;
        private final LayerType layer;

        public EventParameters(StatusType status, CDOID eventID, CDOID measID, LayerType layer) {
            this.status = status;
            this.eventID = eventID;
            this.measID = measID;
            this.layer = layer;
        }

        public StatusType getStatus() {
            return status;
        }

        public CDOID getEventID() {
            return eventID;
        }

        public CDOID getMeasID() {
            return measID;
        }

        public LayerType getLayer() {
            return layer;
        }
    }
}
