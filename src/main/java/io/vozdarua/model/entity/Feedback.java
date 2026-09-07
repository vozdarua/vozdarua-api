package io.vozdarua.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class Feedback extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    public TypeFeedback type;

    @Column(length = 1000)
    public String message;

    public String name;
    public String email;

    @CreationTimestamp
    @Column(updatable = false)
    public Instant createdAt;

    @UpdateTimestamp
    public Instant updatedAt;
}
