package com.careerflow.backend.model;

import com.careerflow.backend.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

   @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
   }

   @Override
    public String getUsername() {
       return email;
   }

   @Override
    public boolean isAccountNonExpired(){
       return true;
   }

   @Override
    public boolean isAccountNonLocked(){
       return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
       return true;
    }

    @Override
    public boolean isEnabled(){
       return true;
    }
}
