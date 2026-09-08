package io.vozdarua.model.dto;

import io.vozdarua.model.entity.Address;
import io.vozdarua.model.entity.Category;
import io.vozdarua.model.entity.Image;
import io.vozdarua.model.entity.Issue;
import io.vozdarua.model.entity.Severity;
import io.vozdarua.model.entity.Status;

import java.time.Instant;

// ponytail: exists only because Issue.reporter is @SecureField(ADMIN) — a plain Issue.list()
// would mask reporter even for the owner's own issues. UserDTO already strips the password,
// so nesting it here is enough; no generic "unmask for owner" mechanism needed.
public record IssueDTO(Long id, String description, Severity severity, Status status,
                        Integer confirmIssue, Integer confirmResolve, boolean anonymous, Category category,
                        Image photo, Address address, UserDTO reporter,
                        Instant createdAt, Instant updatedAt) {

    public static IssueDTO toIssueDTO(Issue issue) {
        return new IssueDTO(issue.id, issue.description, issue.severity, issue.status,
                issue.confirmIssue, issue.confirmResolve, issue.anonymous, issue.category, issue.photo,
                issue.address, issue.reporter != null ? UserDTO.toUserDTO(issue.reporter) : null,
                issue.createdAt, issue.updatedAt);
    }
}
