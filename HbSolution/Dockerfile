# Usar imagem do OpenJDK 17 como base
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar o JAR da aplicação
COPY target/HbSolution-0.0.1-SNAPSHOT.jar /app/HbSolution.jar

# Expor portas do app e do debug
EXPOSE 8080 5005

# Variável opcional para habilitar Swagger em produção (pode ser lida no application.yml)
ENV SPRING_PROFILES_ACTIVE=default
ENV SWAGGER_ENABLED=true

# Instalar curl para healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Healthcheck: tenta endpoint de actuator (habilitar actuator/health no app)
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Rodar backend com debug remoto habilitado
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/HbSolution.jar"]
