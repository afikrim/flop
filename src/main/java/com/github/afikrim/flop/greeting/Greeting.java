package com.github.afikrim.flop.greeting;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

public class Greeting extends RepresentationModel<Greeting> {

    @JsonProperty("name")
    private final String appName;

    @JsonProperty("version")
    private final String appVersion;

    public Greeting(String appName, String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

}
