package io.fiscalizai.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Image extends PanacheEntity {

    private LocalDate occurrenceDate = LocalDate.now();

    public String name;
    public String s3Url;
    public String s3key;
}
