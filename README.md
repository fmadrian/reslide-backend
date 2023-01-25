# reslide-backend

## Description

This repository contains the instructions to install the database and deploy the application's backend.

[Project description](https://github.com/fsv2860/reslide)

[Frontend repository](https://www.github.com/fsv2860/reslide-frontend)

## Installation

### General requirements

1. Install [Java Development Kit](https://www.oracle.com/java) 16.0.1 or superior.

### Initial database setup

**If you're using a service that provides you a PostgreSQL database you can skip the first two steps.**

2. Install [PostgreSQL](https://www.postgresql.org/).
3. Create the database user **'reslide-db-user'** and then the database **'reslide-db'**
   (or you can run the file **dbrole.sql** (database user created doesn't have a password) and then the file **db.sql** located in **sql/functions**).

### Database configuration

4. Change the (backend) configuration file (**application.properties**) to match your database configuration (url,port, user, password).
5. Start the server.

### Creating the SQL functions.

**Wait until the server starts for the first time to follow these steps:**

6. Open the file **'functions.sql'** located in **sql/functions** and replace \<dbuser\> with your database username.
7. Use a database management tool (pgadmin) to run the file.

### Create default admin user

8. Go to **url:8080/api/installation/setup** (8080 can be replaced with the port where the backend is running).
9. The request will create the following admin user:

```
username:admin
password:123456
```

10. The backend is ready to use.

## API documentation

Once you deploy the backend, to access the API documentation go to **url:8080/swagger-ui/** (8080 can be replaced with the port where the backend is running).

## Built with

[Spring Boot](https://spring.io/projects/spring-boot)

[Swagger2](https://swagger.io/)

[PostgreSQL](https://www.postgresql.org/)

[Lombok](https://projectlombok.org/)

[Mapstruct](https://mapstruct.org/)
