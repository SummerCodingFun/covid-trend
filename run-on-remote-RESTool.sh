unzip RESTool2-build.zip
sed -i 's/localhost/44.226.33.29/g' RESTool2/build/config.json
serve -s RESTool2/build
