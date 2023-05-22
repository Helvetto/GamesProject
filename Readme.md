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