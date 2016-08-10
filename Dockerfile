FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/step-tracker-0.0.1-SNAPSHOT-standalone.jar /step-tracker/app.jar

EXPOSE 8080
ENV HOST db
ENV DATABASE step-tracker
ENV DB_USER step-tracker
ENV DB_PASS Buddy1234!

CMD ["java", "-jar", "/step-tracker/app.jar"]
