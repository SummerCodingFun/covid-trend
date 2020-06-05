package io.summercodingfun.covidtrend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Saying {
    private String state;
    private int numberOfCases;
    private int numberOfDeaths;

    public Saying(){

    }
    public Saying(String state, int cases, int deaths){
        this.state = state;
        this.numberOfCases = cases;
        this.numberOfDeaths = deaths;
    }


    @JsonProperty
    public String getState(){
        return state;
    }

    @JsonProperty
    public int getNumberOfCases() {
        return numberOfCases;
    }

    @JsonProperty
    public int getNumberOfDeaths(){
        return numberOfDeaths;
    }

}
