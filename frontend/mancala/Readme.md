# Mancala Web Frontend

The Mancala Web Frontend is a module of the Games Project that provides a user interface for playing the Mancala game.

## Usage

To run the Mancala Web Frontend using Docker, follow these steps:

1. Clone this repository to your local machine.

2. Navigate to the `frontend/mancala` directory:

   ```shell
   cd frontend/mancala
   ```

3. Build the Docker image:

   ```shell
   docker build -t mancala-web-app .
   ```

4. Run the Docker container:

   ```shell
   docker run -p 8000:8000 mancala-web-app
   ```

5. Access the Mancala game frontend:

   Open a web browser and visit [http://localhost:8000](http://localhost:8000) to access the Mancala game frontend.

## TODO
- Find a skilled UI designer and frontend engineer to improve the Mancala game frontend and create a more visually appealing and user-friendly interface.
