FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/pb.jar /pb/app.jar

EXPOSE 4000

CMD ["java", "-jar", "/pb/app.jar"]
