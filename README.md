# covid-trend
A service that plots the trend of covid-19 using NYT data.



To set up database, follow these instructions:

- go to the root of the project
- then type `mysql -uroot -p<Password> [database] < src/main/java/io/summercodingfun/covidtrend/resources/us-states.csv`

if you have this error: `Loading local data is disabled - this must be enabled on both the client and server sides`

- enter mysql by typing `mysql -uroot -p<Password>`
- enter and run `SET GLOBAL local_infile=1;`
- quit mysql by running `exit`
- then enter `mysql --local-infile=1 -uroot -p<password>`
- then enter `SHOW GLOBAL VARIABLES LIKE 'local_infile';`
- ensure `local_infile is ON`
- exit mysql and type `mysql -uroot -p<Password> [database] < src/main/java/io/summercodingfun/covidtrend/resources/us-states.csv`