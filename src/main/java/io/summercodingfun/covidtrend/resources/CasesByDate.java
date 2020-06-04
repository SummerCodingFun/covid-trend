package io.summercodingfun.covidtrend.resources;


public class CasesByDate {
    private String date;
    private Integer cases;

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
