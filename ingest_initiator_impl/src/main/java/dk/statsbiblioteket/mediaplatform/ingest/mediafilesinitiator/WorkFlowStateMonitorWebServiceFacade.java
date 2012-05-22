package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.util.List;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import dk.statsbiblioteket.mediaplatform.workflowstatemonitor.Entity;
import dk.statsbiblioteket.mediaplatform.workflowstatemonitor.State;

public class WorkFlowStateMonitorWebServiceFacade implements WorkFlowStateMonitorFacade {

    private static final Logger log = Logger.getLogger(WorkFlowStateMonitorWebServiceFacade.class);;

    /* (non-Javadoc)
     * @see dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.WorkFlowStateMonitorFacade#getWorkFlowStateForEntity(java.lang.String)
     */
    @Override
    public State getLastWorkFlowStateForEntity(String sbFileId) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource("http://canopus:34080/workflowstatemonitor/");
        
        GenericType<List<Entity>> genericTypeEntities = new GenericType<List<Entity>>() {};
        List<Entity> entities = service.path("entities").get(genericTypeEntities);//path("dr1_20101218100000_20101218110000.mux").queryParam("onlyLast", "true")
        log.info(entities);
    
        GenericType<List<State>> genericTypeStates = new GenericType<List<State>>() {};
        List<State> states = service.path("states").path(sbFileId).queryParam("onlyLast", "true").get(genericTypeStates);
        
        State state = null;
        if (!states.isEmpty()) {
            state = states.get(0);
        }
        return state;
    }

}
