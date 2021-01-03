FROM adoptopenjdk/openjdk11
COPY build/libs/*.jar ./server.jar
RUN java -Djarmode=layertools -jar ./server.jar extract

FROM adoptopenjdk/openjdk11
EXPOSE 8080
WORKDIR /app
COPY --from=0 dependencies/ ./
COPY --from=0 spring-boot-loader/ ./
COPY --from=0 application/ ./
CMD ["java", "org.springframework.boot.loader.JarLauncher"]
