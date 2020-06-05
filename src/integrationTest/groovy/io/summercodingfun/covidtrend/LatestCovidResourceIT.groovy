package io.summercodingfun.covidtrend

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification

class LatestCovidResourceIT extends Specification{
    @Shared
    def client = new RESTClient("http://localhost:8080")

    def 'should respond with 200 code when location is valid' (){
        given:
        String location = "California"

        when: 'the state is valid'
        def response = client.get(path: "/covid-cases/${location}")

        then: 'server returns with 200 code'
        assert response.status == 200 : 'response is 200 when state is valid'
        assert response.responseData['numberOfCases'] == 122917
        assert response.responseData['numberOfDeaths'] == 4484
        assert response.responseData['state'] == 'California'
    }

    def 'should respond with code 400 when location is invalid'(){
        given:
        String location = "Ca"

        when: 'state is invalid'
        def response = client.get(path: "/covid-cases/${location}")

        then: 'server returns 400 code'
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400: 'response code is 400 when location is invalid'
    }
}
