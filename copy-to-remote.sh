scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ./build/distributions/covid-trend.zip ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/covid-cases-prod.yml ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/setup-remote.sh ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/run-on-remote.sh ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/run-on-remote-RESTool.sh ubuntu@44.226.33.29:.
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem ~/src/covid-trend/src/main/java/io/summercodingfun/covidtrend/resources/covidDB.sql ubuntu@44.226.33.29:.
rm RESTool2-build.zip
zip -r RESTool2-build.zip RESTool2/build
scp -i ~/Documents/AWS/LightsailDefaultKey-us-west-2.pem -r ~/src/covid-trend/RESTool2-build.zip ubuntu@44.226.33.29:.
