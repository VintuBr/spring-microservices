FROM java:8-alpine
MAINTAINER Felipe Vinturini <felipe.vinturini@capco.com>
ADD target/balance-service.jar balance-service.jar
ENTRYPOINT ["java", "-jar", "/balance-service.jar"]
EXPOSE 2224
