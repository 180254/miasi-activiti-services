package org.miasi.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class Config {

    public final static String CONFIG_FILENAME = "config.json";

    private final String trelloKey;
    private final String trelloToken;
	private final String githubAddress;

    @JsonCreator
    public Config(@JsonProperty("trelloKey") String trelloKey,
                  @JsonProperty("trelloToken") String trelloToken,
				  @JsonProperty("githubAddress") String githubAddress) {

        this.trelloKey = trelloKey;
        this.trelloToken = trelloToken;
		this.githubAddress = githubAddress;
    }

    public static String configPath() {
        return Paths.get(CONFIG_FILENAME).toAbsolutePath().toString();
    }

    public static Config readFromConfigFile() throws IOException {
        URL resource = Resources.getResource(CONFIG_FILENAME);
        String content = Resources.toString(resource, Charsets.UTF_8);

        return new ObjectMapper().reader(Config.class).readValue(content);
    }

    public String getTrelloKey() {
        return trelloKey;
    }

    public String getTrelloToken() {
        return trelloToken;
    }
	
	public String getGithubAddress() {
		return githubAddress;
	}
}
