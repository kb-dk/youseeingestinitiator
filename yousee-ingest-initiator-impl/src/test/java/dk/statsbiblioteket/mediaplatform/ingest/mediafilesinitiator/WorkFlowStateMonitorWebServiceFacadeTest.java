package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.medieplatform.workflowstatemonitor.State;

public class WorkFlowStateMonitorWebServiceFacadeTest {

    private final Properties defaultProperties;
    private final Logger log;

    public WorkFlowStateMonitorWebServiceFacadeTest() {
        defaultProperties = new Properties();
        defaultProperties.put("hibernate.config.file.path", "/Users/henningbottger/projects/yousee/dev/ingest_component/ingest_initiator_media_file/src/test/config/ingest_initiator_media_files_CLI.hibernate.cfg.xml");
        defaultProperties.put("log4j.config.file.path", "src/test/config/ingest_initiator_media_files_unittest.log4j.xml");
        defaultProperties.put("yousee.recordings.days.to.keep", "28");
        defaultProperties.put("workflow.state.monitor.base.url", "http://canopus:34080/workflowstatemonitor");
        defaultProperties.put("expected.duration.of.file.ingest.process", "12");
        defaultProperties.put("final.work.flow.component.name", "Yousee complete workflow final step");
        defaultProperties.put("work.flow.state.name.done", "Done");
        System.getProperties().put("log4j.defaultInitOverride", "true");
        DOMConfigurator.configure(defaultProperties.getProperty("log4j.config.file.path"));
        log = Logger.getLogger(WorkFlowStateMonitorWebServiceFacadeTest.class);
    }
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetLastWorkFlowStateForEntity_canInitialize() {
        WorkFlowStateMonitorWebServiceFacade workFlowStateMonitorWebServiceFacade = new WorkFlowStateMonitorWebServiceFacade(defaultProperties);
    }

    /** Not a unittest. Requires running webservice and specific state for entity. */
    //@Test
    public void testGetLastWorkFlowStateForEntity_ping() {
        WorkFlowStateMonitorWebServiceFacade workFlowStateMonitorWebServiceFacade = new WorkFlowStateMonitorWebServiceFacade(defaultProperties);
        String sbFileId = "dr1_20101217080000_20101217090000.mux";
        State state = workFlowStateMonitorWebServiceFacade.getLastWorkFlowStateForEntity(sbFileId);
        log.info("Last workflow state: " + state);
        assertEquals(sbFileId, state.getEntity().getName());
    }

    /** Not a unittest. Requires running webservice. */
    //@Test
    public void testGetLastWorkFlowStateForEntity_unknownEntity() {
        WorkFlowStateMonitorWebServiceFacade workFlowStateMonitorWebServiceFacade = new WorkFlowStateMonitorWebServiceFacade(defaultProperties);
        State state = workFlowStateMonitorWebServiceFacade.getLastWorkFlowStateForEntity("impossible_filename.mux");
        assertNull(state);
    }
}
