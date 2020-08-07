package io.summercodingfun.covidtrend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListSaying {
    private List<Saying> list;

    public ListSaying(List<Saying> list) {
        this.list = list;
    }

    @JsonProperty
    public List<Saying> getList() {
        return list;
    }
}
