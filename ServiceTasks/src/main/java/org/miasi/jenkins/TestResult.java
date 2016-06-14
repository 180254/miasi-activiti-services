package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class TestResult implements JavaDelegate{

    @Override
    public void execute( DelegateExecution de ) throws Exception {
        de.setVariable( "reassignTaskToDeveloper", false);
        de.setVariable( "testResults", true);
    }
    
}
