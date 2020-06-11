package io.summercodingfun.covidtrend.resources;

public final class Util {
    private Util(){}

    public static String createKey(String x, String y){
        return String.format("%s:%s", x, y);
    }
}
