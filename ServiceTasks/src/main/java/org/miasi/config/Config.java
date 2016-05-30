package org.miasi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

    @JsonCreator
    public Config(@JsonProperty("trelloKey") String trelloKey,
                  @JsonProperty("trelloToken") String trelloToken) {

        this.trelloKey = trelloKey;
        this.trelloToken = trelloToken;
    }

    public static String configPath() {
        return Paths.get(CONFIG_FILENAME).toAbsolutePath().toString();
    }

    public static Config readFromConfigFile() throws IOException {
        Path path = Paths.get(CONFIG_FILENAME);
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);

        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .reader(Config.class)
                .readValue(content);
    }

    public String getTrelloKey() {
        return trelloKey;
    }

    public String getTrelloToken() {
        return trelloToken;
    }
}
