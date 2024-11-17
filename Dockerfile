FROM openjdk:17-jdk

# JAR 파일 복사
ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} ruuning-mate.jar

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=develop

# 프로덕션용 진입점 설정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "/running-mate.jar"]
