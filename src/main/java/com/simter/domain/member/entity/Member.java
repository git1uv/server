package com.simter.domain.member.entity;


import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 100)
    private String password;

    @Column(length = 20)
    private String nickname;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean hasAirplane;

    @Column(length = 50)
    @ColumnDefault("half")
    private String chatbot;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String loginType;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean mailAlert;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean status;

    private LocalDateTime inactiveDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}