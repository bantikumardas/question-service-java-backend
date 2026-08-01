# ---------- Stage 1: build the fat jar ----------
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

# BuildKit cache mount keeps ~/.m2 warm between CI runs
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B clean package -DskipTests

# ---------- Stage 2: split the jar into layers ----------
FROM eclipse-temurin:21-jdk-alpine AS extract
WORKDIR /build
COPY --from=build /build/target/*-SNAPSHOT.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# ---------- Stage 3: runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

# Ordered least- to most-frequently changed, so Docker caches the big layers
COPY --from=extract --chown=spring:spring /build/extracted/dependencies/ ./
COPY --from=extract --chown=spring:spring /build/extracted/spring-boot-loader/ ./
COPY --from=extract --chown=spring:spring /build/extracted/snapshot-dependencies/ ./
COPY --from=extract --chown=spring:spring /build/extracted/application/ ./

USER spring

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]