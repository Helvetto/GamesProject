# Models and Repositories Module

This module provides classes that represent database entities (models) and repositories for interacting with the
corresponding data. It facilitates data storage and retrieval operations related to the game system.

## Player

The `Player` class represents a player in the game system. It contains the following properties:

- `id`: The unique identifier of the player.
- `activeGames`: A list of the player's active games. Since a player can participate in multiple games simultaneously,
  this list keeps track of the games in which the player is currently involved.

The `Player` class is designed to be extended with additional properties and methods specific to your game system. You
can utilize it to define player-related functionality and relationships in your project.

## PlayerRepository

The `PlayerRepository` is a repository interface that provides an abstraction layer for working with player data using
JPA (Java Persistence API). It allows you to perform common database operations on the `Player` entity, such as
creating, reading, updating, and deleting player records.

You can use the `PlayerRepository` to retrieve player information, manage active games, and perform other player-related
data operations in your project. The repository methods are implemented based on JPA specifications, enabling you to
interact with the underlying database seamlessly.

To use the `PlayerRepository`, you can inject it into your service or controller classes using dependency injection.
Then, you can invoke its methods to interact with player data in your application.

## Game

The `Game` class is an abstract base class representing a game in the system. It provides common properties and methods
for game-related entities. It contains the following properties:

- `id`: The unique identifier of the game.
- `gameType`: An enumeration representing the type of the game. The available game types are defined in the `GameType`
  enum.
- `players`: A list of players participating in the game. It allows for mapping and managing player-game relationships.

To create a new specific game, you can extend the `Game` class and add additional properties, methods, and behavior
specific to the game type. This enables you to define custom game logic and functionality.

### GameType

The `GameType` enum represents the different types of games available in the system. To add a new game type, you can
expand the enum by adding a new value. Each game type can be associated with a specific implementation of the `Game`
class.

Example:

```java
public enum GameType {
    MANCALA,
    CHESS,
    // Add additional game types here
}
```