package io.summercodingfun.covidtrend.resources;


public class CasesByDate {
    private final String date;
    private final Integer cases;

    public CasesByDate(String d, Integer c){
        this.date = d;
        this.cases = c;
    }

    public String getDate(){
        return date;
    }
    public Integer getCases(){
        return cases;
    }
}
