package com.covidcases.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

public class Saying {
    private String state;
    private String numberOfCases;
    private String numberOfDeaths;

    public Saying(){

    }
    public Saying(String state, String cases, String deaths){
        this.state = state;
        this.numberOfCases = cases;
        this.numberOfDeaths = deaths;
    }

    @JsonProperty
    public String getInformation(){
        return state;
    }

    @JsonProperty
    public String getNumberOfCases() {
        return numberOfCases;
    }

    @JsonProperty
    public String getNumberOfDeaths(){
        return numberOfDeaths;
    }
}
