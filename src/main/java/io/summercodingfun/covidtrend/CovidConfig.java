package io.summercodingfun.covidtrend;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class CovidConfig extends Configuration {
    @NotEmpty
    private String template;
    @NotEmpty
    private String defaultLocation = "USA";
    @NotEmpty
    private String infoCases;
    @NotEmpty
    private String infoDeaths;

    @JsonProperty
    public String getTemplate(){
        return template;
    }

    @JsonProperty
    public void setTemplate(String template, String infoCases, String infoDeaths){
        this.template = template;
        this.infoCases = infoCases;
        this.infoDeaths = infoDeaths;
    }

    @JsonProperty
    public String getDefaultLocation(){
        return defaultLocation;
    }

    @JsonProperty
    public void setDefaultLocation(String state){
        this.defaultLocation = state;
    }

    @JsonProperty
    public String getInfoCases(){
        return infoCases;
    }

    @JsonProperty
    public void setInfoCases(String info){
        this.infoCases = info;
    }

    @JsonProperty
    public String getInfoDeaths(){
        return infoDeaths;
    }

    @JsonProperty
    public void setInfoDeaths(String info){
        this.infoDeaths = info;
    }
}
