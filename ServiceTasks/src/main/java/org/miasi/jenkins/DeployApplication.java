package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.fluent.Request;

import java.io.File;

public class DeployApplication implements JavaDelegate {

    // get jar from jenkins and copy it to deploy folder
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String jarUrl = JenkinsConfig.JENKINS_TASK_URL
                + "/ws/" + JenkinsConfig.JAR_BUILD_FILE;

        String savePath = JenkinsConfig.DEPLOY_FOLDER + "/" + JenkinsConfig.JAR_NAME;
        File saveFile = new File(savePath);

        Request.Get(jarUrl)
                .addHeader(JenkinsConfig.authHeader())
                .execute()
                .saveContent(saveFile);
    }

}
