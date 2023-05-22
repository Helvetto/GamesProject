# Mancala API

The `mancala-api` module provides an API for managing the Mancala game based on Mancala rules. The main path mapping for
the API is `/mancala`.

## Technologies Used

- Spring Boot: A Java-based framework used for creating robust and scalable applications.
- PostgreSQL: An open-source relational database management system (RDBMS) known for its reliability and performance.
- Liquibase: A database schema management and versioning tool that helps in managing database changes over time.
- Docker: A containerization platform that allows for easy packaging and deployment of applications, ensuring consistency across different environments.



## Endpoints

The following endpoints are available:

### GET /info

Returns detailed information about the game, including its status, players, board, and pits.

### POST /create

Creates a new game with the specified number of stones in pits. Additionally, it creates a new player for the game and
automatically joins the created game.

### POST /join

Creates a new player and joins the selected game.

### POST /sow

Sows the stones from a specific pit index of the game.

## Dependencies

The `mancala-api` module depends on the following modules:

- `exception`: Contains common exception classes for handling game-related errors.
- `model`: Includes classes representing database entities (models) and repositories.
- `repository`: Provides a base repository interface for working with JPA entities.

# Build Instructions

Follow the steps below to build and run the Mancala API module:

## Prerequisites

- Gradle
- Docker
- Docker Compose

## Build Steps

1. Open a terminal or command prompt.

2. Navigate to the root directory of the `mancala-api` module.

3. Run the following command to build the project using Gradle:

   ```
   gradle build
   ```

   This command will compile the source code, run tests, and package the application.

4. After a successful build, run the following command to build the Docker image:

   ```
   docker build -t mancala-api .
   ```

   This command will create a Docker image for the Mancala API module.

5. Customize the Docker Compose configuration if needed. Open the `docker-compose.yml` file.

To provide custom environment variables, you can pass them as options when running the `docker-compose up` command.
For example:

   ```
   POSTGRES_PASSWORD=mysecretpassword POSTGRES_USER=myuser docker-compose up
   ```

This command will start the Mancala API container and its dependencies using the provided environment variable
values.

Alternatively, you can modify the default values directly in the `docker-compose.yml` file

By modifying these values in the file, the Mancala API container will use the updated environment variable values.

If you have modified the environment variables in the Dockerfile or docker-compose.yml file, ensure that the values
for `DB_HOST`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` in the Dockerfile match the values specified in the
docker-compose.yml file for the PostgreSQL configuration.

Please review and validate the following configurations:

**Dockerfile:**

```dockerfile
ENV DB_HOST=dbpos
ENV DB_NAME=postgres
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
```

**docker-compose.yml:**

```yaml
# Environment variables for the container
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-postgres}
  POSTGRES_USER: ${POSTGRES_USER:-postgres}
  POSTGRES_DB: postgres
  DB_HOST: dbpos
```

Ensure that the values for `DB_HOST`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` are consistent across both files to
avoid any configuration-related issues.

If you have made any modifications to the environment variables, double-check the values to ensure they align correctly.

6. Once the Docker image is built and the Docker Compose configuration is customized (if necessary), use Docker Compose
   to start the Mancala API and its dependencies. Run the following command:

   ```
   docker-compose up
   ```

   This command will start the Mancala API container, the PostgreSQL database container, and any other specified
   services.

7. Access the Mancala API endpoints using the specified paths and HTTP methods. For example, you can make requests
   to `http://localhost:8080/mancala/info` to retrieve game information.

8. To stop the Mancala API and its dependencies, press `Ctrl + C` in the terminal where `docker-compose up` is running.

## TODO

- Enhance session player handling by implementing registration and authorization functionality. This will allow for state persistence in case of backend reloads or when users close their browsers. Additionally, ensure proper security measures using the Spring Security library.

- Refactor the application to remove the CorsFilter dependency and integrate an Nginx proxy into the docker-compose setup. This will provide a more robust and efficient way to handle cross-origin requests.

- Implement Redis caching to improve performance and reduce load on the backend server.

- Integrate Prometheus as the monitoring system to collect and analyze metrics for better insights into the application's performance.

- Set up the ELK Stack (Elasticsearch, Logstash, Kibana) and configure Filebeat to send logs generated by the microservices to Logstash. This pipeline will transfer the logs to an Elasticsearch server, allowing for centralized log management and analysis.

- Integrate Swagger for API documentation to provide a comprehensive and interactive documentation platform.
