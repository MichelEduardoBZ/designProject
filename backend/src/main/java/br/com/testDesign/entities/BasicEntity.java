package br.com.testDesign.entities;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Class for basic entities.
 *
 *  @author Michel Eduardo Bernardo Zeschau
 */
@MappedSuperclass
public abstract class BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    @Version
    private Long version;

    public BasicEntity() {}

    public BasicEntity(long id) {
        this.id = id;
    }

    public BasicEntity(long id, Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
