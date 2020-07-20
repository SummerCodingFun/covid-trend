package io.summercodingfun.covidtrend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.util.List;

public class URLMessage {
    public List<URLList> list;

    public URLMessage(List<URLList> list) {
        this.list = list;
    }

    @JsonProperty
    public List<URLList> getList() {
        return list;
    }
}
