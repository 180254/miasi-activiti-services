package org.miasi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    public final static String CONFIG_FILENAME = "config.json";

    private final String trelloKey;
    private final String trelloToken;
    private final String trelloBoardId;
    private final String activityRestUrl;
    private final String activityUsername;
    private final String activityPassword;
    private final String activityProcessName;

    @JsonCreator
    public Config(@JsonProperty("trelloKey") String trelloKey,
                  @JsonProperty("trelloToken") String trelloToken,
                  @JsonProperty("trelloBoardId") String trelloBoardId,
                  @JsonProperty("activityRestUrl") String activityRestUrl,
                  @JsonProperty("activityUsername") String activityUsername,
                  @JsonProperty("activityPassword") String activityPassword,
                  @JsonProperty("activityProcessName") String activityProcessName) {

        this.trelloKey = trelloKey;
        this.trelloToken = trelloToken;
        this.trelloBoardId = trelloBoardId;
        this.activityRestUrl = activityRestUrl;
        this.activityUsername = activityUsername;
        this.activityPassword = activityPassword;
        this.activityProcessName = activityProcessName;
    }

    public static Config readFromConfigFile() throws IOException {
        Path path = Paths.get(CONFIG_FILENAME);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);

        return new ObjectMapper().readerFor(Config.class).readValue(content);
    }

    public String getTrelloKey() {
        return trelloKey;
    }

    public String getTrelloToken() {
        return trelloToken;
    }

    public String getTrelloBoardId() {
        return trelloBoardId;
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
}
