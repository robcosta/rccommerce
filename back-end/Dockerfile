# Fase de build
FROM maven:3.8.8-eclipse-temurin-17-alpine AS build

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o arquivo pom.xml e o diretório src para o container
COPY pom.xml ./
COPY src ./src

# Instalar as dependências e construir o aplicativo sem compilar ou executar os testes
RUN mvn clean package -Dmaven.test.skip=true

# Fase de execução
FROM eclipse-temurin:17-jre-alpine

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o .jar gerado da fase de build
COPY --from=build /app/target/rccommerce-0.0.1-SNAPSHOT.jar ./rccommerce.jar

# Expor a porta em que a aplicação irá rodar
EXPOSE 8080

# Comando para executar o aplicativo
ENTRYPOINT ["java", "-jar", "rccommerce.jar"]
