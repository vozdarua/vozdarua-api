package io.vozdarua.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.resteasy.reactive.jackson.SecureField;
import io.vozdarua.utils.VozDaRuaUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class Comment extends PanacheEntity {

    @NotBlank
    @Column(length = 1000)
    public String text;

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    public Issue issue;

    @ManyToOne
    @SecureField(rolesAllowed = Roles.ADMIN)
    @JoinColumn(name = "author_id", nullable = true)
    public User author;

    // Snapshot of the author's email for public display; null for anonymous comments.
    // Kept separate from `author` so we never need to expose the full User relation
    // (which would leak User.phone) just to show who wrote a comment.
    // Raw value is never serialized directly — only the masked getter below goes out as JSON,
    // so the full address isn't readable from the API response (Network tab, curl, etc).
    @JsonIgnore
    public String authorEmail;

    @JsonProperty("authorEmail")
    public String getMaskedAuthorEmail() {
        return VozDaRuaUtils.maskEmail(authorEmail);
    }

    @SecureField(rolesAllowed = Roles.ADMIN)
    public String ipAddress;

    @SecureField(rolesAllowed = Roles.ADMIN)
    @Column(length = 500)
    public String userAgent;

    @CreationTimestamp
    @Column(updatable = false)
    public Instant createdAt;
}
