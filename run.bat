@echo off
set JAVA_HOME=C:/Users/Admin/AppData/Local/Temp/jdk17/jdk-17.0.2
set PATH=%JAVA_HOME%/bin;%PATH%
D:/apache-maven-3.9.12/bin/mvn.cmd clean spring-boot:run
