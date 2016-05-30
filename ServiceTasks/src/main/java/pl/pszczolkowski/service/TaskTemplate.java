package pl.pszczolkowski.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class TaskTemplate implements JavaDelegate {

    // private Expression field;
    // is used by process to inject values into service
    
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // do something...

        // String var = (String) delegateExecution.getVariable("someVariable");
        // is used to get a variable from the process

        // delegateExecution.setVariable("input", variable);
        // is used to set a variable for the process

        // String variable = (String) field.getValue(delegateExecution);
        // is used to get value of field
    }

}
