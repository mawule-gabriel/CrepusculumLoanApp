FROM maven:3.9.4-eclipse-temurin-21 as build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/travel-loan-manager-0.0.1-SNAPSHOT.jar travel-loan-manager.jar

EXPOSE 8080

CMD ["java", "-jar", "travel-loan-manager.jar"]
