package io.vozdarua.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

// Records that a given identity (logged-in user id, or an anonymous device id the frontend
// generates) already confirmed/resolved a given issue, so IssueResource can refuse a repeat
// click instead of letting Issue.confirmIssue/confirmResolve be inflated indefinitely.
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"issue_id", "identity", "action"}))
public class IssueAction extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    public Issue issue;

    @Column(nullable = false)
    public String identity;

    /** "CONFIRM" or "RESOLVE". */
    @Column(nullable = false, length = 20)
    public String action;

    @CreationTimestamp
    @Column(updatable = false)
    public Instant createdAt;

    public IssueAction() {
    }

    public IssueAction(Issue issue, String identity, String action) {
        this.issue = issue;
        this.identity = identity;
        this.action = action;
    }

    public static boolean alreadyDone(Long issueId, String identity, String action) {
        return count("issue.id = ?1 and identity = ?2 and action = ?3", issueId, identity, action) > 0;
    }
}
