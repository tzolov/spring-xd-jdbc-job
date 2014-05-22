spring-xd-jdbc
===================

## Build and Installation

Set the environment variable `XD_HOME` to the Spring-XD installation directory

	export XD_HOME=<root-install-dir>/spring-xd/xd

Create Jdbc job module folders:

	mkdir ${XD_HOME}/xd/modules/job/jdbc
	mkdir ${XD_HOME}/xd/modules/job/jdbc/lib
	mkdir ${XD_HOME}/xd/modules/job/jdbc/config
  
Build the Jdbc job jar:

	mvn clean install

Copy the result `jdbc-tasklet-0.0.1-SNAPSHOT.jar` into `${XD_HOME}/xd/modules/job/jdbc/lib`	
	
	cp target/jdbc-tasklet-0.0.1-SNAPSHOT.jar `${XD_HOME}/xd/modules/job/jdbc/lib`

Copy the `jdbc.xml` and `jdbc.properties` module definition into `${XD_HOME}/xd/modules/job/jdbc/config`	
	
	cp src/main/resources/jdbc.* ${XD_HOME}/xd/modules/job/jdbc/config


