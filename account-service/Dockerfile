FROM java:8-alpine
MAINTAINER Felipe Vinturini <felipe.vinturini@capco.com>
ADD target/account-service.jar account-service.jar
ENTRYPOINT ["java", "-jar", "/account-service.jar"]
EXPOSE 2222
