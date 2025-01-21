package com.ulk.readingflow.domain.entities;

import com.ulk.readingflow.domain.enumerations.GenderEnum;
import com.ulk.readingflow.domain.enumerations.StatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
@Entity(name = "USER")
public class User extends AbstractEntity implements UserDetails {

    @Column(
            name = "NAME",
            nullable = false
    )
    private String name;

    @Column(
            name = "USERNAME",
            nullable = false
    )
    private String username;

    @Column(
            name = "EMAIL",
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            name = "PHONE",
            nullable = false
    )
    private String phone;

    @Column(
            name = "PASSWORD",
            nullable = false
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "GENDER",
            nullable = false
    )
    private GenderEnum gender;

    @Column(
            name = "BIRTHDAY",
            columnDefinition = "DATE"
    )
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthday;

    @Column(name = "PROFILE_URL")
    private String profileImageUrl;

    @Column(
            name = "KINDLE_MAIL",
            nullable = false,
            unique = true
    )
    private String kindleMail;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "STATUS",
            nullable = false
    )
    private StatusEnum status;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private Set<UserPreferences> preferences;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : this.roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getDescription().getDescription()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return StatusEnum.EXPIRED.getDescription().equals("EXPIRED");
    }

    @Override
    public boolean isAccountNonLocked() {
        return StatusEnum.BLOCKED.getDescription().equals("BLOCKED");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return StatusEnum.CREDENTIALS_EXPIRED.getDescription().equals("CREDENTIALS_EXPIRED");
    }

    @Override
    public boolean isEnabled() {
        return StatusEnum.ACTIVE.getDescription().equals("ACTIVE");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}
