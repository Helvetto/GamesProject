package com.mygame.repository;

import com.mygame.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base interface for all repositories in the system that work with entities that extend {@link BaseJpaEntity}.
 * <p>
 * Provides a method for obtaining the entity class and a default implementation for finding an entity by its ID or
 * throwing an {@link EntityNotFoundException} if not found.
 *
 * @param <TE> The type of the entity managed by the repository.
 * @param <ID> The type of the ID of the entity.
 */
@NoRepositoryBean
public interface BaseRepository<TE extends BaseJpaEntity<ID>, ID> extends JpaRepository<TE, ID> {
    Class<? super TE> getEntityClass();

    default TE findByIdOrThrow(ID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEntityClass().getSimpleName(), id));
    }
}
