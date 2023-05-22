# Repository Module

The `repository` module provides a base class and interface for working with JPA entities and repositories in the
system.

## BaseJpaEntity

The `BaseJpaEntity` class is a base class for JPA entities that provides basic methods for equality and hash code
calculations, as well as a helper method for retrieving the real class of the entity. It is designed to be extended in
your JPA entity classes.

To use this class, simply extend it in your JPA entity class and provide an implementation for the `getId()` method that
returns the entity's ID.

Example:

```java

@MappedSuperclass
public abstract class BaseJpaEntity<ID> {

    @Id
    private ID id;
}
```

By extending the BaseJpaEntity class, you inherit the provided methods and can leverage them in your entity classes for
common functionality.

## BaseRepository

The BaseRepository interface is a base interface for all repositories in the system that work with entities extending
BaseJpaEntity. It provides a method for obtaining the entity class and a default implementation for finding an entity by
its ID or throwing an EntityNotFoundException if not found.

To use the BaseRepository, extend it in your repository interface and provide the corresponding entity class and ID
type.

Example:

```java

@NoRepositoryBean
public interface BaseRepository<TE extends BaseJpaEntity<ID>, ID> extends JpaRepository<TE, ID> {

}
```

The BaseRepository interface extends the JpaRepository interface. You can also define additional custom methods in the
repository interface to handle specific data access requirements for your entities.

By utilizing the BaseRepository, you can benefit from the provided methods and leverage JPA functionality in your
repository implementations.