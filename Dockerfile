# 1 Stage, copy and compile code
FROM maven:3.5-alpine as builder

# Add commons from github and install it -------------------
RUN apk add --no-cache openssl
RUN wget https://github.com/nicolasvp/commons/releases/download/v1.0/commons.tar.gz \
    && tar -xzvf commons.tar.gz \
    && rm commons.tar.gz
RUN cd commons && mvn clean install -DskipTests=true

# Copy microservice source code and compile it -------------------
COPY . /microservice
RUN cd microservice && mvn clean package -DskipTests=true

# 2 Stage, import jar and run it
FROM openjdk:8-alpine
MAINTAINER nicolasverapalominos@gmail.com
LABEL version=1.0
LABEL description="Delivery microservice"
LABEL vendor="Nicolas"
COPY --from=builder /microservice/target/delivery-0.0.1-SNAPSHOT.jar /opt/delivery-microservice.jar
CMD java -jar /opt/delivery-microservice.jar