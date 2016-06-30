
package org.miasi.unittests;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;


public class UnitTest implements JavaDelegate{

    @Override
    public void execute( DelegateExecution de ) throws Exception {
        de.setVariable("reassignTaskToDeveloper", false);
        de.setVariable( "testResults", true);
    }
    
    

}
