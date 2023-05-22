package com.mygame.repository;

import java.util.Objects;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.util.ProxyUtils;

/**
 * This class is a base class for JPA entities that provides basic methods for equality and hash code calculations,
 * as well as a helper method for retrieving the real class of the entity.
 * To use this class, simply extend it in your JPA entity class and provide an implementation for the {@code getId()}
 * method that returns the entity's ID.
 */
@MappedSuperclass
public abstract class BaseJpaEntity<ID> {

    public abstract ID getId();

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseJpaEntity)) {
            return false;
        }
        BaseJpaEntity that = (BaseJpaEntity) o;
        if (!this.getRealClass().isInstance(that)) {
            return false;
        }
        return this.identityEquals(that);
    }

    @Override
    public int hashCode() {
        return identityHashCode();
    }

    @Override
    public String toString() {
        return identityString();
    }


    static String identityStringForClass(Class<?> clazz, Object id) {
        return clazz.getSimpleName() + "[" + id + "]";
    }

    String identityString() {
        return identityStringForClass(getRealClass(), getId());
    }

    boolean identityEquals(BaseJpaEntity<ID> other) {
        return other != null && getId() != null && Objects.equals(this.getId(), other.getId());
    }

    int identityHashCode() {
        return getId() == null ? System.identityHashCode(this) : getId().hashCode();
    }

    Class<?> getRealClass() {
        return ProxyUtils.getUserClass(this);
    }
}
