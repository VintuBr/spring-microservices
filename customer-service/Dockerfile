FROM java:8-alpine
MAINTAINER Felipe Vinturini <felipe.vinturini@capco.com>
ADD target/customer-service.jar customer-service.jar
ENTRYPOINT ["java", "-jar", "/customer-service.jar"]
EXPOSE 3333
