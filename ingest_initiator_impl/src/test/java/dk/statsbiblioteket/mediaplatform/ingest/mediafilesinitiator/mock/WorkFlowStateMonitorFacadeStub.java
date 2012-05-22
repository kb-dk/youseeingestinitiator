package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock;

import java.util.Date;

import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.WorkFlowStateMonitorFacade;
import dk.statsbiblioteket.mediaplatform.workflowstatemonitor.Entity;
import dk.statsbiblioteket.mediaplatform.workflowstatemonitor.State;

public class WorkFlowStateMonitorFacadeStub implements
        WorkFlowStateMonitorFacade {

    @Override
    public State getLastWorkFlowStateForEntity(String sbFileId) {
        State state = new State();
        state.setComponent("Yousee complete workflow final step");
        state.setDate(new Date());
        Entity entity = new Entity();
        entity.setName("dr1_20101217080000_20101217090000.mux");
        state.setEntity(entity);
        state.setMessage("Message");
        state.setStateName("Completed");
        return state;
    }

}
