package org.miasi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

public class Config {

    public final static String CONFIG_FILENAME = "config.json";

    private final String activityRestUrl;
    private final String activityUsername;
    private final String activityPassword;
    private final String activityProcessName;
    private final String activityPushTaskId;
    private final String gitPath;

    @JsonCreator
    public Config(@JsonProperty("activityRestUrl") String activityRestUrl,
                  @JsonProperty("activityUsername") String activityUsername,
                  @JsonProperty("activityPassword") String activityPassword,
                  @JsonProperty("activityProcessName") String activityProcessName,
                  @JsonProperty("activityPushTaskId") String activityPushTaskId,
                  @JsonProperty("gitPath") String gitPath) {

        this.activityRestUrl = activityRestUrl;
        this.activityUsername = activityUsername;
        this.activityPassword = activityPassword;
        this.activityProcessName = activityProcessName;
        this.activityPushTaskId = activityPushTaskId;
        this.gitPath = gitPath;
    }

    public static Config readFromConfigFile() throws IOException {
        URL resource = Resources.getResource(CONFIG_FILENAME);
        String content = Resources.toString(resource, Charsets.UTF_8);
        return new ObjectMapper().readerFor(Config.class).readValue(content);
    }

    public String getActivityRestUrl() {
        return activityRestUrl;
    }

    public String getActivityUsername() {
        return activityUsername;
    }

    public String getActivityPassword() {
        return activityPassword;
    }

    public String getActivityProcessName() {
        return activityProcessName;
    }

    public String getActivityPushTaskId() {
        return activityPushTaskId;
    }

    public String getGitPath() {
        return gitPath;
    }
}
