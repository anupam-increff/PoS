package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Base entity with optimistic locking using @Version annotation.
 * 
 * Optimistic Locking: Uses version field to detect concurrent modifications.
 * - When entity is updated, version is incremented automatically
 * - If two transactions try to update same entity, the second will fail with OptimisticLockException
 * - Better performance as no locks are held during transaction
 * - Suitable for read-heavy applications with infrequent conflicts
 * 
 * Pessimistic Locking: Can be enabled using @Lock annotation or EntityManager.lock()
 * - Acquires database locks preventing concurrent access
 * - Higher data consistency but potential performance impact
 * - Use LockModeType.PESSIMISTIC_WRITE or PESSIMISTIC_READ
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = ZonedDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
} 