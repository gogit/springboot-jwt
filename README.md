Spring Boot project to create and validate JWT tokens

https://medium.com/@pradeep_thomas/securing-microservices-using-spring-boot-and-json-web-tokens-efea98404f66

https://medium.com/@pradeep_thomas/securing-microservices-using-spring-boot-and-json-web-tokens-continued-e453389edaba

## Building it

Clone the repo and build it using

jwt$ mvn clean package

## Running the microservice

jwt$ java -jar target/jwt-0.0.1-SNAPSHOT.jar

## Create Token

HTTP POST

http://localhost:8081/api/token/create?username=John

Above call returns a JWT token for the username John

<p>
  <img src="./create.png" alt="Create a Token">
</p>


## Validate Token

To validate the above token use

HTTP POST

http://localhost:8081/api/token/validate?token=eyJhbGciOiJIUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWyiwuVrJSKsnIzMsuSUxX0lHKTCxRsjI0NbewNDIyMjPRUUqtKIAKmBiABUqLU4vyEnNTgfqy8jPylGoBbDA8zUYAAAA.XnjmWWaB9xSqG_lKJCL8Res1o5qeX1-1h2uYfPX82os

<p>
  <img src="./validate.png" alt="Validate a Token">
</p>
