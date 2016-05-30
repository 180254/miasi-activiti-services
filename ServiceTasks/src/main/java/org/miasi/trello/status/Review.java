package org.miasi.trello.status;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class Review implements JavaDelegate {

    public void execute(DelegateExecution delegateExecution) throws Exception {
        String var = (String) delegateExecution.getVariable(TrelloStatus.VAR_NAME);
        new TrelloStatus().change(var, getClass().getSimpleName());
    }
}
