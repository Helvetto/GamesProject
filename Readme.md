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

   ```shell
   docker-compose -f mancala-docker-compose/docker-compose.yml up
   ```

   The game web UI will be accessible at [http://localhost:8000](http://localhost:8000).

If you want to play this game on the same device, you should open the URL in incognito mode or in a different browser.

### Creating and Starting a New Game

1. To create a new game and start it, click the "Start Game" button.
2. Enter the desired number of stones (must be 4 or more) into the input field.
3. Press "Enter" or click the "Create Game" button.
4. Voila! You have created the game. Take note of the game ID, which can be found in the upper left corner of the screen or in the URL parameters.
5. Share the game ID with another player to invite them to join your game.

### Joining a Game

1. To join an existing game, enter the game ID provided by the other player into the input field.
2. Click the "Join Game" button.
3. The game will automatically start, and the first player's turn will be randomly determined.

Enjoy playing the game with your friends!