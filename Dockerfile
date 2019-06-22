FROM java:8-jre-alpine

RUN apk add --no-cache curl

COPY target/spring-boot-admin-docker.jar /app/service.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/app/service.jar"]

