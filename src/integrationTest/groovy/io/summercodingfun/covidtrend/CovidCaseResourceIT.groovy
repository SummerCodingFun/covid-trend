package io.summercodingfun.covidtrend

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class CovidCaseResourceIT extends Specification {
    @Shared
    def client = new RESTClient("http://localhost:8080")

    def 'should return 200 code when state and date are valid' (){
        given:
        String location = 'California'
        String date = '2020-06-03'

        when: 'state and date are valid'
        def response = client.get(path: "/covid-cases/${location}/${date}")

        then: 'server returns 200 code'
        assert response.status == 200 : 'response code is 200 when state and date are valid'
        assert response.responseData['numberOfCases'] == 120407
        assert response.responseData['numberOfDeaths'] == 4422
        assert response.responseData['state'] == 'California'
    }

    def 'should return 400 code when state is invalid' (){
        given:
        String location = 'CA'
        String date = '2020-06-03'

        when: 'state is invalid'
        client.get(path: "/covid-cases/${location}/${date}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400: 'response code should be 400 when state and date are invalid'
    }

    def 'should return 400 code when date is invalid' () {
        given:
        String location = 'California'
        String date = '123-4502'

        when: 'date is invalid'
        client.get(path: "/covid-cases/${location}/${date}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }
}
