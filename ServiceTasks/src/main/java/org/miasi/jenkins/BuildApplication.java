package org.miasi.jenkins;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.http.client.fluent.Request;
import org.awaitility.Awaitility;

public class BuildApplication implements JavaDelegate {

    // run task on jenkins and wait until build done
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String buildUrl = JenkinsConfig.JENKINS_TASK_URL
                + "/build";
        String statusUrl = JenkinsConfig.JENKINS_TASK_URL
                + "/lastBuild/api/json?pretty=true";

        // run task
        Request.Post(buildUrl)
                .addHeader(JenkinsConfig.authHeader())
                .execute().returnContent().asString();

        // wait until build done
        Awaitility.await()
                .atMost(3, TimeUnit.MINUTES)
                .pollInterval(5, TimeUnit.SECONDS)
                .until(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return isBuilding(statusUrl);
                    }
                });
        
        Thread.sleep( 500);
        delegateExecution.setVariable( "buildFailed", false);
    }

    public static boolean isBuilding(String statusUrl) throws IOException {
        try {
            String response = Request.Get(statusUrl)
                    .addHeader(JenkinsConfig.authHeader())
                    .execute().returnContent().asString();

            JSONObject json = new JSONObject(response);
            return json.getBoolean("building");
        } catch(Exception e) {
            return false;
        }
    }

}
