package com.erp.auth.domain.entity;

import com.erp.auth.domain.event.UserLoggedInEvent;
import com.erp.common.domain.AggregateRoot;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_user")
public class User extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;        // 해시된 값만 저장
    private Long employeeId;

    @ElementCollection
    @CollectionTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_code")
    private Set<String> roleCodes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    protected User() {}

    public static User register(String username, String hashedPassword, Long employeeId) {
        User u = new User();
        u.username = username;
        u.password = hashedPassword;
        u.employeeId = employeeId;
        u.status = Status.ACTIVE;
        return u;
    }

    public void assignRole(String roleCode) { this.roleCodes.add(roleCode); }
    public void revokeRole(String roleCode) { this.roleCodes.remove(roleCode); }

    public boolean login(String rawPassword, PasswordHasher hasher) {
        if (status != Status.ACTIVE) return false;
        boolean ok = hasher.matches(rawPassword, password);
        if (ok) register(new UserLoggedInEvent(this.id, this.username, Instant.now()));
        return ok;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Set<String> getRoleCodes() { return roleCodes; }
    public Status getStatus() { return status; }

    public enum Status { ACTIVE, LOCKED }

    @FunctionalInterface
    public interface PasswordHasher {
        boolean matches(String raw, String hashed);
    }
}