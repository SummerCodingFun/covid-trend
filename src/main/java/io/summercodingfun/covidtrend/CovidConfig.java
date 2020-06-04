package io.summercodingfun.covidtrend;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class CovidConfig extends Configuration {
    @NotEmpty
    private String template;
    @NotEmpty
    private String defaultLocation = "USA";

    @JsonProperty
    public String getTemplate(){
        return template;
    }

    @JsonProperty
    public void setTemplate(String template, String infoCases, String infoDeaths, String trend){
        this.template = template;
    }

    @JsonProperty
    public String getDefaultLocation(){
        return defaultLocation;
    }

    @JsonProperty
    public void setDefaultLocation(String state){
        this.defaultLocation = state;
    }
}
