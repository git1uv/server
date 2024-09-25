FROM openjdk:21

# JAR 파일을 정확히 지정
ARG JAR_FILE=build/libs/simter-0.0.1-SNAPSHOT.jar

# JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} /app/app.jar

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

