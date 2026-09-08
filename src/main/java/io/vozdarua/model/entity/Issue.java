package io.vozdarua.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.resteasy.reactive.jackson.SecureField;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
public class Issue extends PanacheEntity {

    @Column(length = 1000)
    public String description;

    @ManyToOne
    @JoinColumn(name = "severity_id")
    public Severity severity;

    @ManyToOne()
    @JoinColumn(name = "status_id")
    public Status status;

    public Integer confirmIssue = 0;

    public Integer confirmResolve = 0;

    public boolean anonymous;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    public Image photo;

    @ManyToOne
    @SecureField(rolesAllowed = Roles.ADMIN)
    @JoinColumn(name = "reporter_id", updatable = false, nullable = true)
    public User reporter;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    public Address address;

    @CreationTimestamp
    @Column(updatable = false)
    public Instant createdAt;

    @UpdateTimestamp
    public Instant updatedAt;

    public static Issue findByIdWithCategoryAndTags(Long id) {
        return Issue.find("SELECT i FROM Issue i LEFT JOIN FETCH i.category c LEFT JOIN FETCH c.tags WHERE i.id = ?1", id).firstResult();
    }

    public static Issue findByIdWithStatus(Long id) {
        return Issue.find("SELECT i FROM Issue i LEFT JOIN FETCH i.status s WHERE i.id = ?1", id).firstResult();
    }

    public static List<Object[]> rankingByReporter() {
        return getEntityManager()
            .createQuery("SELECT i.reporter.id, i.reporter.phone, i.reporter.email, i.reporter.role, COUNT(i) FROM Issue i WHERE i.reporter IS NOT NULL GROUP BY i.reporter.id, i.reporter.phone, i.reporter.email, i.reporter.role ORDER BY COUNT(i) DESC LIMIT 5", Object[].class)
            .getResultList();
    }

    // Only counts issues whose address.cityRef was resolved (best-effort match at creation
    // time) - issues whose free-text city didn't match a seeded City are excluded here.
    // Uses an explicit "LEFT JOIN i.status st" (not the dotted i.status.name path) so
    // issues with a null status still count towards the total - a dotted path on a
    // nullable association compiles to an inner join in JPQL and would silently drop
    // them (same pitfall already noted in UserResource.meStats).
    public static List<Object[]> rankingByCity() {
        return getEntityManager()
            .createQuery("SELECT c.id, c.name, s.uf, COUNT(i), SUM(CASE WHEN st.name = 'Resolvido' THEN 1L ELSE 0L END) " +
                "FROM Issue i JOIN i.address a JOIN a.cityRef c JOIN c.state s LEFT JOIN i.status st " +
                "GROUP BY c.id, c.name, s.uf ORDER BY COUNT(i) DESC LIMIT 10", Object[].class)
            .getResultList();
    }
}
