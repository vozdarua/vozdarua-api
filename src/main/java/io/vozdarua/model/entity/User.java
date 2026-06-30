package io.vozdarua.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.resteasy.reactive.jackson.SecureField;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Optional;

@Entity
@Table(name = "vozdaruauser")
public class User extends PanacheEntity {

    @Column(unique = true)
    public String phone;

    @Email
    @Column(unique = true)
    public String email;

    @SecureField(rolesAllowed = Roles.ADMIN)
    public String password;
    public String role;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static Optional<User> findByEmailOrPhone(String email, String phone) {
        return find("lower(email) = lower(?1) or phone = ?2", email, phone).firstResultOptional();
    }
}
