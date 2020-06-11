DROP DATABASE IF EXISTS
    covid_data;

CREATE DATABASE covid_data;

use covid_data;

CREATE TABLE usStates(
    date varchar(11),
    state varchar(30),
    fips int,
    cases int,
    deaths int,
    PRIMARY KEY(date, state)
);

LOAD DATA LOCAL INFILE '/Users/claire/Desktop/covid-trend/us-states.csv'
INTO TABLE usStates
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(date, state, fips, cases, deaths)