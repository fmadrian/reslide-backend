# reslide-backend

## Description

Reslide is a web point of sale that allows clients to manage information about: users, clients, products, invoices, orders, payments, product brands, measurement types, payment methods, individual types, contact types, and product types.
This repository contains the application's backend files necessary to manage the database and deploy the backend.

App demo: [https://m6q1cn5p.herokuapp.com/](https://m6q1cn5p.herokuapp.com/)

**NOTE: Demo might be in maintenance mode in order to save up usage quota.**

Frontend code: [https://www.github.com/fsv2860/reslide-frontend](https://www.github.com/fsv2860/reslide-frontend)

## Deployment

Follow these steps to deploy the backend.

### Initial database setup

**If you're using a service that provides you a PostgreSQL database you can skip the first two steps.**

1. Install [PostgreSQL](https://www.postgresql.org/).
2. Create the database user **'reslide-db-user'** and then the database **'reslide-db'**
   (or you can run the file **dbrole.sql** (database user created doesn't have a password) and then the file **db.sql** located in **sql/functions**).

### Database configuration

3. Change the (backend) configuration file (**application.properties**) to match your database configuration (url,port, user, password).
4. Start the server.

### Creating the SQL functions.

**Wait until the server starts for the first time to follow these steps:**

5. Open the file **'functions.sql'** located in **sql/functions** and replace \<dbuser\> with your database username.
6. Use a database management tool (pgadmin) to run the file.

### Create default admin user

7. Go to **url:8080/api/installation/setup** (8080 can be replaced with the port where the backend is running).
8. The request will create the following admin user:

```
username:admin
password:123456
```

9. The backend is ready to use.

## API documentation

Once you deploy the backend, to access the API documentation go to **url:8080/swagger-ui/** (8080 can be replaced with the port where the backend is running).

## Built with

[Spring Boot](https://spring.io/projects/spring-boot)

[Swagger2](https://swagger.io/)

[PostgreSQL](https://www.postgresql.org/)

[Lombok](https://projectlombok.org/)

[Mapstruct](https://mapstruct.org/)
