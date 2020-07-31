package io.summercodingfun.covidtrend;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DbConfig {
    private String user;
    private String password;
    private String url;

    @JsonProperty
    public String getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String passwordk) {
        this.password = passwordk;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public void setUrl(String url) {
        this.url = url;
    }
}
