FROM openjdk:8
ADD /TestDocker/TestdockerKube/target/testdockerkube.jar testdockerkube.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","testdockerkube.jar" ]