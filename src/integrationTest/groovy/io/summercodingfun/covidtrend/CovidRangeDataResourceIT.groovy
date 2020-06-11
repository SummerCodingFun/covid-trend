package io.summercodingfun.covidtrend

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class CovidRangeDataResourceIT extends Specification{

    @Shared
    def client = new RESTClient("http://localhost:8080")

    def 'should return 200 code when state and starting date are valid and range is negative'() {
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
            it[0]['deaths'] == 4484
            it[1]['date'] == '2020-06-03'
            it[1]['cases'] == 120407
            it[1]['deaths'] == 4422
            it[2]['date'] == '2020-06-02'
            it[2]['cases'] == 118081
            it[2]['deaths'] == 4360
            it[3]['date'] == '2020-06-01'
            it[3]['cases'] == 115643
            it[3]['deaths'] == 4287
        }
    }

    def 'should return 200 code when state and starting date are valid and range is positive'() {
        given:
        String location = 'California'
        String startingDate = '2020-05-04'
        int range = 3;

        when: 'state and starting date are valid'
        def response = client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 200 code'
        assert response.status == 200: 'response code is 200 when state and starting date are valid'
        assert response.responseData['state'] == 'California'
        with (response.responseData.data) {
            it[0]['date'] == '2020-05-04'
            it[0]['cases'] == 56333
            it[0]['deaths'] == 2297
            it[1]['date'] == '2020-05-05'
            it[1]['cases'] == 58848
            it[1]['deaths'] == 2386
            it[2]['date'] == '2020-05-06'
            it[2]['cases'] == 60787
            it[2]['deaths'] == 2478
            it[3]['date'] == '2020-05-07'
            it[3]['cases'] == 62481
            it[3]['deaths'] == 2561
        }
    }

    def 'should return 200 code when state and starting date are valid and range is 0'() {
        given:
        String location = 'California'
        String startingDate = '2020-06-04'
        int range = 0;

        when: 'state and starting date are valid'
        def response = client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 200 code'
        assert response.status == 200: 'response code is 200 when state and starting date are valid'
        assert response.responseData['state'] == 'California'
        with (response.responseData.data) {
            it[0]['date'] == '2020-06-04'
            it[0]['cases'] == 122917
            it[0]['deaths'] == 4484
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
        assert e.response.status == 400 : 'state is invalid'
        assert e.response.responseData['message'] == 'Please enter a valid state'
    }

    def 'should return 400 when date is invalid'(){
        given:
        String location = 'California'
        String startingDate = '2020-1-01'
        int range = -3;

        when: 'starting date is invalid'
        client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400 : 'date is invalid'
        assert e.response.responseData['message'] == 'state, starting date, or range is invalid'
    }

    def 'should return 400 when range is too low'(){
        given:
        String location = 'California'
        String startingDate = '2020-06-04'
        int range = -1000;

        when: 'starting range is invalid'
        client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400 : 'range is too low'
        assert e.response.responseData['message'] == 'state, starting date, or range is invalid'
    }

    def 'should return 400 when range is too high'(){
        given:
        String location = 'California'
        String startingDate = '2020-06-04'
        int range = 1000;

        when: 'starting range is invalid'
        client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400 : 'range is too high'
        assert e.response.responseData['message'] == 'state, starting date, or range is invalid'
    }

    def 'should return 400 when range is not a number'(){
        given:
        String location = 'California'
        String startingDate = '2020-06-04'
        String range = "apples";

        when: 'starting range is not a number'
        client.get(path: "/covid-range-data/${location}/${startingDate}/${range}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400 : 'range must be a number'
        assert e.response.responseData['message'] == 'range must be a number'
    }
}
