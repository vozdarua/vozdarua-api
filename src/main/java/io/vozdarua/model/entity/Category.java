package io.vozdarua.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
public class Category extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String name;

    public String icon;

    @Column(length = 500)
    public String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "category_tags", joinColumns = @JoinColumn(name = "category_id"))
    @Column(name = "tag")
    public List<String> tags;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
