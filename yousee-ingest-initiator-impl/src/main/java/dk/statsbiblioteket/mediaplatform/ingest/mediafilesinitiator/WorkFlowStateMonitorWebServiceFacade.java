package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import dk.statsbiblioteket.medieplatform.workflowstatemonitor.State;

import javax.ws.rs.core.MediaType;

public class WorkFlowStateMonitorWebServiceFacade implements WorkFlowStateMonitorFacade {

    private static final String WORKFLOW_STATE_MONITOR_BASE_URL_KEY = "workflow.state.monitor.base.url";
    private static final Logger log = Logger.getLogger(WorkFlowStateMonitorWebServiceFacade.class);
    private static final GenericType<List<State>> genericTypeStateList = new GenericType<List<State>>() {};

    private final String workFlowStateMonitorBaseUrl;

    public WorkFlowStateMonitorWebServiceFacade(Properties properties) {
        this.workFlowStateMonitorBaseUrl = properties.getProperty(WORKFLOW_STATE_MONITOR_BASE_URL_KEY);
        if (this.workFlowStateMonitorBaseUrl == null) {
            throw new RuntimeException("Missing property: " + WORKFLOW_STATE_MONITOR_BASE_URL_KEY);
        }
    }
    
    /* (non-Javadoc)
     * @see dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.WorkFlowStateMonitorFacade#getWorkFlowStateForEntity(java.lang.String)
     */
    @Override
    public State getLastWorkFlowStateForEntity(String sbFileId) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource webResource = client.resource(workFlowStateMonitorBaseUrl).path("states").path(sbFileId).queryParam("onlyLast", "true");
        List<State> states = webResource.get(genericTypeStateList);
        log.debug("Found states: " + states);
        State state = null;
        if (!states.isEmpty()) {
            state = states.get(0);
        }
        return state;
    }

    @Override
    public void addState(String stateName, String message) {
        State state = new State();
        state.setComponent("Yousee Ingest Initiator");
        state.setStateName(stateName);
        state.setMessage(message);

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource webResource = client.resource(workFlowStateMonitorBaseUrl).path("states").path(
                "Yousee Ingest Initiator");
        webResource.type(MediaType.TEXT_XML_TYPE).post(state);
        log.debug("Added state: " + state);
    }

}
