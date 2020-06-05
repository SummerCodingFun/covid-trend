package io.summercodingfun.covidtrend

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class CovidRangeDataResourceIT extends Specification{

    @Shared
    def client = new RESTClient("http://localhost:8080")

    def 'should return 200 code when state and starting date are valid'() {
        given:
        String location = 'California'
        String startingDate = '2020-06-04'
        int range = -3;

        when: 'state and starting date are valid'
        def response = client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 200 code'
        assert response.status == 200: 'response code is 200 when state and starting date are valid'
        assert response.responseData['state'] == 'California'
        with (response.responseData.data) {
            it[0]['date'] == '2020-06-04'
            it[0]['cases'] == 122917
            it[1]['date'] == '2020-06-03'
            it[1]['cases'] == 120407
            it[2]['date'] == '2020-06-02'
            it[2]['cases'] == 118081
            it[3]['date'] == '2020-06-01'
            it[3]['cases'] == 115643
        }
    }

    def 'should return 400 when state is invalid'(){
        given:
        String location = 'CA'
        String startingDate = '2020-06-04'
        int range = -3;

        when: 'state is invalid'
        client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def 'should return 400 when date is invalid'(){
        given:
        String location = 'California'
        String startingDate = '06-04-2020'
        int range = -3;

        when: 'starting date is invalid'
        client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }
}
