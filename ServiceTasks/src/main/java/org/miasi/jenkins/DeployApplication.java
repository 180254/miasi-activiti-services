package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.fluent.Request;

import java.io.File;

public class DeployApplication implements JavaDelegate {

    // get jar from jenkins and copy it to deploy folder
    // will NOT throw exception if fail
    // sets variable: deployFailed = true|false
    public void execute(DelegateExecution de) throws Exception {

        try {
            String buildId = (String) de.getVariable("jenkins_build_id");

            String jarUrl = JenkinsConfig.JENKINS_TASK_URL
                    + "/ws/" + buildId + "/" + JenkinsConfig.JAR_NAME;

            String savePath = JenkinsConfig.DEPLOY_FOLDER + "/" + JenkinsConfig.JAR_NAME;
            File saveFile = new File(savePath);

            Request.Get(jarUrl)
                    .addHeader(JenkinsConfig.authHeader())
                    .execute()
                    .saveContent(saveFile);

            de.setVariable("deployFailed", false);
        } catch (Exception ex) {
            de.setVariable("deployFailed", true);
        }
    }
}
