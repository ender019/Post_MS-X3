FROM eclipse-temurin:23-jdk as builder
WORKDIR /app
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "/app/*.jar"]