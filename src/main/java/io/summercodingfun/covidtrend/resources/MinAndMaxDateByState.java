package io.summercodingfun.covidtrend.resources;

import org.joda.time.DateTime;

public class MinAndMaxDateByState {
    private DateTime maxDate;
    private DateTime minDate;

    public MinAndMaxDateByState(){
        maxDate = new DateTime();
        minDate = new DateTime();
    }

    public MinAndMaxDateByState(DateTime minDate, DateTime maxDate){
        this.maxDate = maxDate;
        this.minDate = minDate;
    }

    public DateTime getMaxDate(){
        return maxDate;
    }

    public void setMaxDate(DateTime d){
        maxDate = d;
    }

    public DateTime getMinDate(){
        return minDate;
    }

    public void setMinDate(DateTime d){
        minDate = d;
    }
}
