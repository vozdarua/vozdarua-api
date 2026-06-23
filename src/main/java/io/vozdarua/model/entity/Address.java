package io.vozdarua.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class Address extends PanacheEntity {

    @NotNull
    @Column(nullable = false)
    public Double latitude;

    @NotNull
    @Column(nullable = false)
    public Double longitude;

    @Column(length = 2)
    public String state;

    public String city;
    public String neighborhood;
    public String street;
    public String cep;
    public String number; //We may have letter also, like nº 45A

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
