spring-xd-jdbc
===================

Simple [Jdbc] Job module for [Spring-XD].

## Build and Installation

Set the environment variable `XD_HOME` to the Spring-XD installation directory

	export XD_HOME=<root-install-dir>/spring-xd/xd

Create Jdbc job module folders:

	mkdir ${XD_HOME}/modules/job/jdbc
	mkdir ${XD_HOME}/modules/job/jdbc/lib
	mkdir ${XD_HOME}/modules/job/jdbc/config
  
Build the Jdbc job jar:

	mvn clean install

Copy the result `xd-jdbc-module-0.0.1-SNAPSHOT.jar` into `${XD_HOME}/modules/job/jdbc/lib`	
	
	cp target/xd-jdbc-module-0.0.1-SNAPSHOT.jar `${XD_HOME}/modules/job/jdbc/lib`

Copy the `jdbc.xml` and `jdbc.properties` module definition into `${XD_HOME}/modules/job/jdbc/config`	
	
	cp src/main/resources/jdbc.* ${XD_HOME}/modules/job/jdbc/config

## Usage

#### SQL script

	xd>job create jdbcTest2 --definition "jdbc 
		--driverClassName=org.postgresql.Driver 
		--url=jdbc:postgresql://phd1:5432/gpadmin 
		--username=gpadmin --password='' 	
		--script='file:/<path to your SQL script>/test1.sql'" --deploy 

##### Embedded SQL command		

	xd>job create jdbcTest2 --definition "jdbc 
		--driverClassName=org.postgresql.Driver 
		--url=jdbc:postgresql://phd1:5432/gpadmin 
		--username=gpadmin --password='' 	
		--sql='CREATE TABLE analytics AS SELECT employ_id, count(*) FROM Company GROUP BY employ_id;'" --deploy 
