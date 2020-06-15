package io.summercodingfun.covidtrend.resources;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CasesAndDeathsByDate {
    private final DateTime date;
    private final Integer cases;
    private final Integer deaths;

    public CasesAndDeathsByDate(DateTime d, Integer c, Integer dd){
        this.date = d;
        this.cases = c;
        this.deaths = dd;
    }

    public String getDate(){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        return fmt.print(date);
    }
    public Integer getCases(){
        return cases;
    }
    public Integer getDeaths() {
        return deaths;
    }
}
