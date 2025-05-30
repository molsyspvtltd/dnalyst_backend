package com.example.app_server.Roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class RoleUser implements UserDetails {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "managed_by")
    @JsonIgnore  // Prevent infinite recursion
    private RoleUser managedBy;

    @OneToMany(mappedBy = "managedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Prevent infinite recursion
    private List<RoleUser> staffMembers;

    @ManyToOne
    @JoinColumn(name = "sub_admin_id")
    @JsonIgnore  // Prevent infinite recursion
    private RoleUser subAdmin;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + role.name());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
