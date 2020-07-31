scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ./build/distributions/covid-trend.zip ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/covid-cases-prod.yml ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/run-on-remote.sh ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/src/main/java/io/summercodingfun/covidtrend/resources/covidDB.sql ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/src/main/java/io/summercodingfun/covidtrend/resources/us-states.csv ubuntu@44.226.33.29:.
