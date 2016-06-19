package org.miasi.jenkins;

import com.google.common.io.BaseEncoding;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class JenkinsConfig {

    public static String JENKINS_AUTH[] = {"user", "user"};
    public static String JENKINS_TASK_URL = "http://127.0.0.1:8080/jenkins/job/test/";

    public static String JAR_NAME = "lab1_1-0.0.1-SNAPSHOT.jar";
    public static String JAR_BUILD_FILE = "/target/lab1_1-0.0.1-SNAPSHOT.jar";

    public static String BACKUP_FOLDER = "C:\\Users\\Adrian\\Desktop\\App\\backup";
    public static String DEPLOY_FOLDER = "C:\\Users\\Adrian\\Desktop\\App\\prod";

    public static Header authHeader() {
        String activityBasic = JENKINS_AUTH[0] + ":" + JENKINS_AUTH[1];
        String activityBasic64 = BaseEncoding.base64().encode(activityBasic.getBytes());
        String headerVal = "Basic " + activityBasic64;
        return new BasicHeader("Authorization", headerVal);
    }
}
