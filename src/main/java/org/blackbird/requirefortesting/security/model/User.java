package org.blackbird.requirefortesting.security.model;

import jakarta.persistence.*;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  private boolean enabled;
  private Collection<? extends GrantedAuthority> authorities;

  @Column(nullable = false, unique = true)
  private String email;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true; // Implement logic based on your application's requirements
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // Implement logic based on your application's requirements
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // Implement logic based on your application's requirements
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
