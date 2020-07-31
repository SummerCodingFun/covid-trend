use covid_data;

CREATE TABLE update_table(
    theDate Date,
    state varchar(30),
    fips int,
    cases int,
    deaths int
);

LOAD DATA LOCAL INFILE 'src/main/java/io/summercodingfun/covidtrend/resources/us-states.csv'
INTO TABLE update_table
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(theDate, state, fips, cases, deaths);

UPDATE usStates
INNER JOIN update_table on update_table.theDate = usStates.theDate AND update_table.state = usStates.state
SET usStates.cases = update_table.cases;

UPDATE usStates
INNER JOIN update_table on update_table.theDate = usStates.theDate AND update_table.state = usStates.state
SET usStates.deaths = update_table.deaths;

DROP TABLE update_table;