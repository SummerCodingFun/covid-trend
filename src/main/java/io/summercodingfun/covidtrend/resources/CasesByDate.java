package io.summercodingfun.covidtrend.resources;

import org.joda.time.DateTime;

public class CasesByDate {
    public final int cases;
    public final DateTime date;

    public CasesByDate(int cases, DateTime date){
        this.cases = cases;
        this.date = date;
    }

    public DateTime getDate(){
        return date;
    }
    public int getCases(){
        return cases;
    }

}
