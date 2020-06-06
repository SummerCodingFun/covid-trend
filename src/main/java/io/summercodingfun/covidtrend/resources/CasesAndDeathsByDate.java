package io.summercodingfun.covidtrend.resources;


public class CasesAndDeathsByDate {
    private final String date;
    private final Integer cases;
    private final Integer deaths;

    public CasesAndDeathsByDate(String d, Integer c, Integer dd){
        this.date = d;
        this.cases = c;
        this.deaths = dd;
    }

    public String getDate(){
        return date;
    }
    public Integer getCases(){
        return cases;
    }
    public Integer getDeaths() {
        return deaths;
    }
}
