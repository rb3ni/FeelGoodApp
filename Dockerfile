FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
COPY target/FeelGoodApp-1.0-SNAPSHOT.jar /opt/app/FeelGoodApp.jar
CMD ["java", "-jar", "/opt/app/FeelGoodApp.jar"]