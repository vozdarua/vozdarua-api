package io.fiscalizai.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;

@Entity
public class Issue extends PanacheEntity {

    private LocalDate occurrenceDate = LocalDate.now();

    public String description;
    public Severity severity;
    public Status status;
    public Integer confirmIssue;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;

    @OneToOne
    @JoinColumn(name = "photo_id")
    public Image photo;

    @OneToOne
    @JoinColumn(name = "reporter_id")
    public User reporter;

    @ManyToOne
    @JoinColumn(name = "address_id")
    public Address address;

}
