package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import dk.statsbiblioteket.mediaplatform.workflowstatemonitor.State;

/** Hides interface to work flow state monitor */
public interface WorkFlowStateMonitorFacade {

    /** Retrieves last state of the entity
     * 
     * @return state or null if entity does not exist. 
     */
    public abstract State getLastWorkFlowStateForEntity(String sbFileId);

}