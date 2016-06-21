package org.miasi.jenkins;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.http.client.fluent.Request;
import org.awaitility.Awaitility;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class BuildApplication implements JavaDelegate {

    // run task on jenkins and wait until build done
    // will NOT throw exception if fail
    // sets variable: buildFailed = true|false
    public void execute(DelegateExecution de) throws Exception {

        try {
            String lastStatusUrl = JenkinsConfig.JENKINS_TASK_URL + "/lastBuild/api/json?pretty=true";
            String lastBuildNumber = getBuildNumber(lastStatusUrl);

            String newBuildNumber = plus(lastBuildNumber, 1);
            de.setVariable("jenkins_build_id", newBuildNumber);

            String buildUrl = JenkinsConfig.JENKINS_TASK_URL + "/build";
            runTask(buildUrl);

            String customStatusUrl = JenkinsConfig.JENKINS_TASK_URL + "/" + newBuildNumber + "/api/json?pretty=true";
            Awaitility.await()
                    .atMost(3, TimeUnit.MINUTES)
                    .pollDelay(5, TimeUnit.SECONDS)
                    .pollInterval(5, TimeUnit.SECONDS)
                    .until(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !isBuilding(customStatusUrl);
                        }
                    });

            de.setVariable("buildFailed", false);

        } catch (Exception ex) {
            de.setVariable("buildFailed", true);
        }
    }


    public static void runTask(String buildUrl) throws IOException {
        Request.Post(buildUrl)
                .addHeader(JenkinsConfig.authHeader())
                .execute().returnContent().asString();
    }

    public static String getBuildNumber(String statusUrl) throws IOException {
        String response = Request.Get(statusUrl)
                .addHeader(JenkinsConfig.authHeader())
                .execute().returnContent().asString();

        JSONObject json = new JSONObject(response);
        return json.getString("number");
    }

    public static boolean isBuilding(String statusUrl) {
        try {
            String response = Request.Get(statusUrl)
                    .addHeader(JenkinsConfig.authHeader())
                    .execute().returnContent().asString();

            JSONObject json = new JSONObject(response);
            return json.getBoolean("building");
        } catch (IOException ex) {
            return true;
        }
    }

    public static String plus(String a, int b) {
        return String.valueOf(Integer.parseInt(a) + b);
    }
}
