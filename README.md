
# E-commerce Spring Application

This is a fully built Spring boot application example for internet sales.
- Spring boot version: 3.4.2
- Java version: 21
- Maven version: 3.9.6
- PostgreSQL version: 15

## Main Features

- Monolithic and 3 tier architecture (controller, service, repository)
- JPA implementation with PostgresSQL database, with several relationship types, fetType examples and CascadeTypes. Additionally, it includes pagination implementation.
- Lombok implementation.
- Hibernate implementation using validations. 
- DTO design pattern, complementing it with moddelMapper to do the transformations.
- REST implementation, using the basic verbs.
- Exception handling through @RestControllerAdviser and custom exception handling.
- Logging implementation, using slf4j
- Spring Security implementation, with a custom authenticator using JWT. Cookie based authentication as well implemented
- Use of atomic transactions with @Transactional


## Set up 
In order to set up this project and test it locally pls:
- Clone the project
- In the application.properties change the database credentials to access ur local database
- Import the postman collection 
- Use maven commands to install dependencies 

