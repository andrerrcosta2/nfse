FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/nfse-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]
