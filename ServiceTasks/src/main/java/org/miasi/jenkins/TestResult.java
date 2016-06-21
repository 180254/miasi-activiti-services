package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.fluent.Request;

// check if task on jenkins is in success status
// will NOT throw exception if fail
// sets variable: buildFailed
public class TestResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution de) throws Exception {
        try {
            de.setVariable("reassignTaskToDeveloper", false);

            String buildId = (String) de.getVariable("jenkins_build_id");
            String resultUrl = JenkinsConfig.JENKINS_TASK_URL
                    + "/" + buildId + "/consoleText";

            String response = Request.Get(resultUrl)
                    .addHeader(JenkinsConfig.authHeader())
                    .execute().returnContent().asString();

            de.setVariable("testResults", isTestOK(response));

        } catch (Exception ex) {
            de.setVariable("testResults", false);
        }
    }

    public boolean isTestOK(String consoleText) {
        return consoleText.contains("Finished: SUCCESS");
    }

}
