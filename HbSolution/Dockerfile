# Usar imagem do OpenJDK 17 como base
FROM openjdk:17-jdk-slim

# Definir diretório de trabalho no contêiner
WORKDIR /app

# Copiar o arquivo JAR gerado para o contêiner
COPY target/HbSolution-0.0.1-SNAPSHOT.jar /app/HbSolution.jar

# Expor a porta que o backend vai rodar (por exemplo, 8080)
EXPOSE 8080

# Comando para rodar o aplicativo
CMD ["java", "-jar", "/app/HbSolution.jar"]
