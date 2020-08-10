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

    private DbConfig database;

    private String host;
    private int port;

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

    @JsonProperty("sundial")
    public SundialConfiguration getSundialConfiguration() {
        return sundialConfiguration;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
