# Games Project

The Games Project is a web application that allows users to play various games online. It currently includes the
implementation of the Mancala game. The project consists of two main directories: `backend` and `frontend`.

## Backend

The `backend` directory contains the backend code for the Games Project. It includes the following modules:

### mancala-api

The `mancala-api` module is the backend module for the Mancala game. It provides a RESTful API for managing game
sessions, players, and game actions.

For detailed instructions on building and running the `mancala-api` module, please refer to the README.md file in
the `backend/mancala/mancala-api` directory.

## Frontend

The `frontend` directory contains the frontend code for the Games Project. It includes the following modules:

### mancala-web

The `mancala-web` module is the frontend module for the Mancala game. It provides a web interface for playing the
Mancala game.

For detailed instructions on building and running the `mancala-web` module, please refer to the README.md file in
the `frontend/mancala` directory.

Sure! Here's an updated version of the README.md file with the requested changes:

# Getting Started

[Mancala Game Application](#mancala-game-application)

# Mancala Game Application

This guide will walk you through the steps to run the Mancala Game Application, including the backend and frontend.

## Prerequisites

- Docker installed on your machine
- Gradle (for building the backend)

## Steps

1. Build the game API module:

   ```shell
   gradle build -p backend/mancala/mancala-api
   ```

2. Build the Docker image for the game API backend:

   ```shell
   docker build -t mancala-api backend/mancala/mancala-api/.
   ```

3. Build the Docker image for the game web frontend:

   ```shell
   docker build -t mancala-web frontend/mancala/.
   ```

4. Start the Docker containers using Docker Compose:

To run the application with an Nginx proxy, you may encounter some issues related to file access. To resolve this, you
have two options:

Option 1: Allowing Docker to Open Your Files

1. Ensure that the necessary permissions are granted for Docker to access your files.
2. Start the Docker containers using the following command:

```shell
docker-compose -f mancala-docker-compose/docker-compose.yml up
```

Option 2: Running Docker Compose without Nginx

1. Execute the following command to start the containers without Nginx:

```shell
docker-compose -f mancala-docker-compose/without-nginx/docker-compose.yml up
```

Please choose the option that suits your requirements and system setup.

Note: Regardless of the option you choose, the game web UI will be accessible at http://localhost:8000.

If you want to play this game on the same device, you should open the URL in incognito mode or in a different browser.

### Creating and Starting a New Game

1. To create a new game and start it, click the "Start Game" button.
2. Enter the desired number of stones (must be 4 or more) into the input field.
3. Press "Enter" or click the "Create Game" button.
4. Voila! You have created the game. Take note of the game ID, which can be found in the upper left corner of the screen
   or in the URL parameters.
5. Share the game ID with another player to invite them to join your game.

### Joining a Game

1. To join an existing game, enter the game ID provided by the other player into the input field.
2. Click the "Join Game" button.
3. The game will automatically start, and the first player's turn will be randomly determined.

Enjoy playing the game with your friends!

## Troubleshooting

If you encounter problems while running the application, you can try the following troubleshooting steps:

1. **Force Restart Docker Compose**: If you experience issues with the application, try relaunching Docker Compose with
   the `--force-recreate` option. This command forces the recreation of containers, which can help resolve certain
   inconsistencies. Run the following command:

   ```shell
   docker-compose -f mancala-docker-compose/docker-compose.yml up --force-recreate
   ```

2. **Delete Existing Images and Containers**: Sometimes, conflicts with existing images or containers can cause
   problems. To eliminate this possibility, you can delete all existing images and containers related to the
   application. Use the following commands to remove them:

   ```shell
   docker-compose -f mancala-docker-compose/docker-compose.yml down
   docker ps -a | grep 'mancala-' | awk '{print $1}' | xargs docker rm
   docker images | grep 'mancala-' | awk '{print $3}' | xargs docker rmi
   ```

   Caution: The above commands remove all containers and images with names containing 'mancala-'. Make sure you don't
   have any important data stored in other containers or images before executing them.

3. **Run Containers Independently**: Instead of using Docker Compose, you can try running the backend and frontend
   containers independently. Use the following steps:

   a. Run the backend container using Docker Compose:
   ```shell
   docker-compose -f backend/mancala/mancala-api/docker-compose.yml up
   ```

   b. Build and run the frontend container using the Dockerfile:
   ```shell
   cd frontend/mancala
   docker build -t mancala-web .
   docker run -p 8000:8000 mancala-web
   ```

4. **Alternative Tools**: If the above steps don't resolve the issue, you can try running the application using IntelliJ
   IDEA or any other suitable development tool. Keep in mind that you need to configure the database connection and
   update it in the application properties accordingly.

Remember to consult the application's documentation or seek assistance if the troubleshooting steps don't resolve your
problem.