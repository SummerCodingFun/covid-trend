package io.summercodingfun.covidtrend.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.summercodingfun.covidtrend.resources.CasesAndDeathsByDate;

import java.util.List;

public class CovidRangeData {
    private String state;
    private List<CasesAndDeathsByDate> data;

    public CovidRangeData() {
    }

    public CovidRangeData(String s, List<CasesAndDeathsByDate> d){
        this.state = s;
        data = d;
    }

    @JsonProperty
    public String getState(){
        return state;
    }

    @JsonProperty
    public List<CasesAndDeathsByDate> getData(){
        return data;
    }
}
