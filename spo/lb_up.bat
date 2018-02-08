@echo off
echo ==== liquibase update
call mvn org.liquibase:liquibase-maven-plugin:3.2.0:update -Pprod -Ddb.username=%1 -Ddb.password=%1
echo ==== complete
