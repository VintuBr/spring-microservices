FROM java:8-alpine
MAINTAINER Felipe Vinturini <felipe.vinturini@capco.com>
ADD target/gateway-service.jar gateway-service.jar
ENTRYPOINT ["java", "-jar", "/gateway-service.jar"]
EXPOSE 8765
