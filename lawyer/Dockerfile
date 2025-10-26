FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY .env .

RUN java --version
RUN chmod +x ./mvnw

RUN ./mvnw package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "/app/target/lawyer-0.0.1-SNAPSHOT.jar"]