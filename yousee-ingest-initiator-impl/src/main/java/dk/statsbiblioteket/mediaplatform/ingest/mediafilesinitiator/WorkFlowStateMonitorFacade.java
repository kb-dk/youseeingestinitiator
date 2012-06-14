package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import dk.statsbiblioteket.medieplatform.workflowstatemonitor.State;

/** Hides interface to work flow state monitor */
public interface WorkFlowStateMonitorFacade {

    /** Retrieves last state of the entity
     * 
     * @return state or null if entity does not exist. 
     */
    public abstract State getLastWorkFlowStateForEntity(String sbFileId);

    /** Add a state for this component
     * @param stateName Name of state to add.
     * @param message A human readable message for the state.
     */
    public abstract void addState(String stateName, String message);
}