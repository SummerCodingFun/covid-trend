package io.summercodingfun.covidtrend.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.summercodingfun.covidtrend.resources.CasesByDate;

import java.util.List;

public class CovidRangeData {
    private String state;
    private List<CasesByDate> data;

    public CovidRangeData() {
    }

    public CovidRangeData(String s, List<CasesByDate> d){
        this.state = s;
        data = d;
    }

    @JsonProperty("State")
    public String getState(){
        return state;
    }

    @JsonProperty("Data")
    public List<CasesByDate> getData(){
        return data;
    }
}
