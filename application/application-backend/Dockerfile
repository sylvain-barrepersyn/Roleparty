FROM eclipse-temurin:25-jre

RUN useradd --system --no-create-home roleparty
WORKDIR /app
COPY application-backend-jvm-executable.jar backend.jar

USER roleparty
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "backend.jar"]
