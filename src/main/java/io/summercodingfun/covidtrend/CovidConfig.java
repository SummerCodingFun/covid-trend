package io.summercodingfun.covidtrend;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;

public class CovidConfig extends Configuration {
    @NotEmpty
    private String template;

    private DbConfig database;

    @JsonProperty
    public DbConfig getDatabase() {
        return database;
    }

    @JsonProperty
    public void setDatabase(DbConfig database) {
        this.database = database;
    }

    @JsonProperty
    public String getTemplate(){
        return template;
    }

    @JsonProperty
    public void setTemplate(String template, String infoCases, String infoDeaths, String trend){
        this.template = template;
    }
}
