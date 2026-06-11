package io.fiscalizai.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Address extends PanacheEntity {

    public Double latitude;
    public Double longitude;
    public String location;

}
