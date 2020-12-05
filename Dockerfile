FROM openjdk:11
##Criando uma variavel de ambiento do tipo ARG
###uma espécie de variável de ambiente dentro do processo de build da imagem
ARG JAR_FILE=target/proposta-0.0.1-SNAPSHOT.jar
##Copiando a variável de ambiente acima para a imagem e renomeando para app.jar
COPY ${JAR_FILE} app.jar
###ENTRYPOINT permite que instruir como nossa aplicação vai rodar, no caso o nosso jar
ENTRYPOINT ["java","-jar","/app.jar"]

