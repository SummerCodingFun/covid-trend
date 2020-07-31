# covid-trend
A service that plots the trend of covid-19 using NYT data.



To set up database, follow these instructions:

- go to the root of the project
- then type `mysql -uroot -p < src/main/java/io/summercodingfun/covidtrend/resources/covidDB.sql`

if you have this error: `Loading local data is disabled - this must be enabled on both the client and server sides`

- enter mysql by typing `mysql -uroot -p`
- enter and run `SET GLOBAL local_infile=1;`
- quit mysql by running `exit`
- then enter `mysql --local-infile=1 -uroot -p`
- then enter `SHOW GLOBAL VARIABLES LIKE 'local_infile';`
- ensure `local_infile is ON`
- exit mysql and type `mysql --local-infile=1 -uroot -p < src/main/java/io/summercodingfun/covidtrend/resources/covidDB.sql`

if you have this error: `Client does not support authentication protocol requested by server; consider upgrading MySQL client`
- enter into mysql by typing: `mysql -uroot -p<password>`
- enter and run `ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '<password>';`
- enter and run `flush privileges;`