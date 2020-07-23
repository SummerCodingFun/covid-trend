package io.summercodingfun.covidtrend;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.dropwizard.sundial.SundialConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CovidConfig extends Configuration {
    @NotEmpty
    private String template;
    @Valid
    @NotNull
    private SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @JsonProperty
    public String getTemplate(){
        return template;
    }

    @JsonProperty
    public void setTemplate(String template, String infoCases, String infoDeaths, String trend){
        this.template = template;
    }

    @JsonProperty("sundial")
    public SundialConfiguration getSundialConfiguration() {
        return sundialConfiguration;
    }
}
