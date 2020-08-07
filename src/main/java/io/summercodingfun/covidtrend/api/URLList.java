package io.summercodingfun.covidtrend.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public class URLList {
    public URL url;

    public URLList(URL url) {
        this.url = url;
    }

    @JsonProperty
    public URL getURL() {
        return url;
    }
}
