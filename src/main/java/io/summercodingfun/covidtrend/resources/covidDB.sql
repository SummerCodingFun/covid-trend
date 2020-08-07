DROP DATABASE IF EXISTS
    covid_data;

CREATE DATABASE covid_data;

use covid_data;

CREATE TABLE usStates(
    theDate Date,
    state varchar(30),
    fips int,
    cases int,
    deaths int,
    PRIMARY KEY(theDate, state)
);

LOAD DATA LOCAL INFILE '/tmp/us-states.csv'
INTO TABLE usStates
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(theDate, state, fips, cases, deaths);