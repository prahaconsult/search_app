FROM ubuntu:latest
LABEL authors="sam"

ENTRYPOINT ["top", "-b"]

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

# 4. Otevřeme port 8080, na kterém naše aplikace běží
EXPOSE 8080

# 5. Spustíme aplikaci
ENTRYPOINT ["java", "-jar", "app.jar"]