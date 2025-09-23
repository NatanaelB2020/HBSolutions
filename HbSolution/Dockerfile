# Usar imagem do OpenJDK 17 como base
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/HbSolution-0.0.1-SNAPSHOT.jar /app/HbSolution.jar

# Expor portas do app e do debug
EXPOSE 8080 5005

# Rodar backend com debug remoto habilitado
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/HbSolution.jar"]
