package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.fluent.Request;

public class TestResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution de) throws Exception {
        de.setVariable("reassignTaskToDeveloper", false);

        String resultUrl = JenkinsConfig.JENKINS_TASK_URL
                + "/lastBuild/consoleText";

        String response = Request.Get(resultUrl)
                .addHeader(JenkinsConfig.authHeader())
                .execute().returnContent().asString();

        de.setVariable("testResults", isTestOK(response));
    }

    public boolean isTestOK(String consoleText) {
        return !consoleText.contains("FAILED");
    }

}
