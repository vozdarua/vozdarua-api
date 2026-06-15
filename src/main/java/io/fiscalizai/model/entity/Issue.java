package io.fiscalizai.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Optional;

@Entity
public class Issue extends PanacheEntity {

    @Column(length = 1000)
    public String description;

    public Severity severity;
    public Status status;
    public Integer confirmIssue;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    public Image photo;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    public FiscalizaiUser reporter;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    public Address address;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static Issue findByIdWithCategoryAndTags(Long id) {
        return Issue.find("SELECT i FROM Issue i LEFT JOIN FETCH i.category c LEFT JOIN FETCH c.tags WHERE i.id = ?1", id).firstResult();
    }
}
