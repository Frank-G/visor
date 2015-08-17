package de.uniulm.omi.cloudiator.visor.reporting.mc;

import de.uniulm.omi.cloudiator.paasage.EventParameter;
import de.uniulm.omi.cloudiator.paasage.MeasurementParameter;
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

    private Map<String, MeasurementParameter> cacheMetrics = new HashMap<String, MeasurementParameter>(); // <monitorinstance_id, params>
    private Map<String, EventParameter> cacheEvents = new HashMap<String, EventParameter>(); // <monitorinstance_id, params>
    private final String resourceName;
    private final String modelName;
    private final CdoClientWrapper cdo;

    private MetricCollectorCache(String resourceName, String modelName){
        this.modelName = modelName;
        this.resourceName = resourceName;
        this.cdo = new CdoClientWrapper(new CDOClient(), modelName); /* TODO config variables in file*/
    }

    public static synchronized MetricCollectorCache create(String resourceName, String modelName){
        if(singleton == null){
            singleton = new MetricCollectorCache(resourceName, modelName);
        }
        return singleton;
    }

    public MeasurementParameter getMeasurementParameter(String id){
        for(Map.Entry<String, MeasurementParameter> s : cacheMetrics.entrySet()){
            if(s.getKey().equals(id)){
                return s.getValue();
            }
        }

        // if not returned, ip not found in set
        MetricStorage.MeasurementType type = cdo.getMeasurementType(id);

        MeasurementParameter params = new MeasurementParameter(
            cdo.getObjectOfMetricInstance(id), // CDOID metricInstanceID,
            cdo.getObjectOfExecutionContext(id), // CDOID execContextInstanceID,
            type, // MetricStorage.MeasurementType measurementType,
            cdo.getMeasurementObject(type, id), // CDOID measurementObject1,
            null //TODO not used currently: CDOID measurementObject2
        );
        cacheMetrics.put(id, params);
        return params;
    }

    public EventParameter getEventParameter(String id){
        for(Map.Entry<String, EventParameter> s : cacheEvents.entrySet()){
            if(s.getKey().equals(id)){
                return s.getValue();
            }
        }

        StatusType status = StatusType.SUCCESS; //TODO how to handle status?
        CDOID eventID = cdo.getRandomSimpleEvent(id); //cdo.getObjectOfEvent(id);
        CDOID measID = cdo.getRandomMeasurementToOneOfTheMetrics(id); // TODO what if no measurement is linked to an event?
        LayerType layer  = LayerType.IAA_S; // TODO calculate from most highest of all Metrics?!

        EventParameter params = new EventParameter(
            status,
            eventID,
            measID,
            layer
        );
        cacheEvents.put(id, params);
        return params;
    }

    public void refresh(){
        for(Map.Entry<String, MeasurementParameter> p : cacheMetrics.entrySet()){
            //TODO
        }
    }

    public String getModelName() {
        return modelName;
    }

    public String getResourceName() {
        return resourceName;
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

        public CDOID getRandomSimpleEvent(String name){
            Event ev = this.getEvent(name);

            if(ev instanceof FunctionalEvent){
                FunctionalEvent se = (FunctionalEvent) ev;
                return se.cdoID();
            } else if (ev instanceof NonFunctionalEvent){
                NonFunctionalEvent se = (NonFunctionalEvent) ev;
                return se.cdoID();

            } else if (ev instanceof BinaryEventPattern){
                BinaryEventPattern se = (BinaryEventPattern) ev;

                //trace down till simple event
                return getRandomSimpleEvent(se.getLeftEvent().getName());
            } else if (ev instanceof UnaryEventPattern){
                UnaryEventPattern se = (UnaryEventPattern) ev;

                //trace down till simple event
                return getRandomSimpleEvent(se.getEvent().getName());
            }

            //TODO exception handling
            return null;
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
            EList<EObject> objs = getModelResourceConteents();


            for(EObject obj : objs) {
                LOGGER.info("Search for metriccontext: " + mc.getName());

                // Get Camel Model
                CamelModel model = (CamelModel) obj;

                for(MetricInstance mi : model.getMetricModels().get(0).getMetricInstances()){
                    if(mi.getMetricContext().getName().equals(mc.getName())){
                        for(Measurement m : model.getExecutionModels().get(0).getMeasurements()){
                            if(m.getMetricInstance().getName().equals(mi.getName())){
                                return m.cdoID();
                            }
                        }
                    }
                }
            }

            return null; //TODO exception?
        }

        public EList<EObject> getModelResourceConteents(){
            try{
                return view.getResource(resourceName).getContents();
            } catch(Exception ex){
                view = this.cdoClient.openView();
                return view.getResource(resourceName).getContents();
            }
        }

        //TODO cache result
        public MetricInstance getMetricInstance(String metricInstance){
            EList<EObject> objs = getModelResourceConteents();
            for(EObject obj : objs) {
                if(obj instanceof CamelModel && ((CamelModel)obj).getName().equals(modelName)){
                    LOGGER.info("The objs stored are: " + obj.toString());

                    // Get Camel Model
                    CamelModel model = (CamelModel) obj;

                    for(MetricInstance mi : model.getMetricModels().get(0).getMetricInstances()){
                        if(mi.getName().equals(metricInstance)){
                            return mi;
                        }
                    }
                } else {
                    LOGGER.info("Model not of Type CamelModel and/or different name: " + obj.toString());
                }
            }

            return null; //TODO exception?
        }

        //TODO cache result
        public Event getEvent(String name){
            EList<EObject> objs = getModelResourceConteents();
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
}
