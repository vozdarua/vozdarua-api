package io.vozdarua.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class Issue extends PanacheEntity {

    @Column(length = 1000)
    public String description;

    @ManyToOne
    @JoinColumn(name = "severity_id")
    public Severity severity;

    @ManyToOne
    @JoinColumn(name = "status_id")
    public Status status;

    public Integer confirmIssue;

    public boolean anonymous;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    public Image photo;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    public User reporter;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    public Address address;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public static Issue findByIdWithCategoryAndTags(Long id) {
        return Issue.find("SELECT i FROM Issue i LEFT JOIN FETCH i.category c LEFT JOIN FETCH c.tags WHERE i.id = ?1", id).firstResult();
    }
}
