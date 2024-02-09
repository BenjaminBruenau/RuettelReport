FROM bennib99/spark:3.5.0-17-alpine
WORKDIR /app
#COPY /target/scala-3.3.1/Analysis-assembly-0.1.0.jar ./analysis.jar
#CMD ["java", "-jar", "analysis.jar"]
COPY /target/scala-3.3.1/Analysis-assembly-0.1.0.jar /opt/spark/jars/analysis.jar
CMD ["java", "-cp", "/opt/spark/jars/analysis.jar", "run"]

