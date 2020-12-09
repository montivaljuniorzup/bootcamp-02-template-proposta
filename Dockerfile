##O build esta sendo feito em dois estágios. No primeiro está sendo usado o maven como base.
# Está sendo feito o build da aplicação e gerando o artefato
#Criando uma imagem do maven para fazer o build do projeto e a essa imagem estou dando o apelido de builder
FROM maven:latest AS builder
#Copiando a pasta src do meu projeto para dentro da pasta src da imagem
COPY src /usr/src/app/src
#copiando o pom para fazer o download das dependencias e colando na raiz do projeto no container
COPY pom.xml /usr/src/app
#comando para gerar o artefato do projeto
RUN mvn -f /usr/src/app/pom.xml clean package

##Fazendo o Build da aplicação em si, encima do artefato  criado no passo anterior
FROM openjdk:11
#Copiando o artefado gerado em "builder",/usr/src/app/target/proposta-0.0.1-SNAPSHOT.jar,
# e copiando na imagem que está sendo criada porém dando o nome de proposta ao artefato  /usr/app/proposta.jar
COPY --from=builder /usr/src/app/target/proposta-0.0.1-SNAPSHOT.jar /usr/app/app.jar
EXPOSE 8080
#Perdi uma hora aqui por que não copiei o caminho inteiro.... "/usr/app/app.jar"
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]

#FROM openjdk:11
##Criando uma variavel de ambiento do tipo ARGdocker
###uma espécie de variável de ambiente dentro do processo de build da imagem
#ARG JAR_FILE=target/proposta-0.0.1-SNAPSHOT.jar
##Copiando a variável de ambiente acima para a imagem e renomeando para app.jar
#COPY ${JAR_FILE} app.jar
#EXPOSE 8080
###ENTRYPOINT permite que instruir como nossa aplicação vai rodar, no caso o nosso jar
#ENTRYPOINT ["java","-jar","/app.jar"]
