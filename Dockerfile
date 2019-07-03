FROM java:8-jre-alpine

RUN apk add --no-cache curl

COPY target/spring-boot-admin-docker.jar /app/admin/service.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/app/admin/service.jar"]

