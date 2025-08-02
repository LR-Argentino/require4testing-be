package org.blackbird.requirefortesting.security.internal;

import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.security.internal.repository.UserRepository;
import org.blackbird.requirefortesting.security.model.CreateUserDto;
import org.blackbird.requirefortesting.security.model.User;
import org.blackbird.requirefortesting.security.model.UserBatchDto;
import org.blackbird.requirefortesting.security.model.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostgresUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findUserByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));
  }

  public User loadUserByEmail(String email) throws UsernameNotFoundException {
    return userRepository
        .findUserByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
  }

  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream().map(PostgresUserDetailsService::mapToUserDto).toList();
  }

  @Transactional(readOnly = true)
  public Map<Long, UserBatchDto> getUsersByIds(Set<Long> userIds) {
    List<User> users = userRepository.findAllById(userIds);
    Map<Long, UserBatchDto> userMap =
        users.stream()
            .collect(Collectors.toMap(User::getId, PostgresUserDetailsService::mapToUserBatchDto));
    return userMap;
  }

  @Transactional(readOnly = true)
  public UserDto getUserById(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    return mapToUserDto(user);
  }

  @Transactional
  public User registerUser(CreateUserDto createUserDto) {
    Optional<User> existingUserByUsername =
        userRepository.findUserByUsername(createUserDto.username());
    Optional<User> isEmailExists = userRepository.findUserByEmail(createUserDto.email());

    if (existingUserByUsername.isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }
    if (isEmailExists.isPresent()) {
      throw new IllegalArgumentException("Email already exists");
    }
    List<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + createUserDto.role().getRoleName()));

    User user =
        User.builder()
            .username(createUserDto.username())
            .password(passwordEncoder.encode(createUserDto.password()))
            .email(createUserDto.email())
            .authorities(authorities)
            .enabled(true)
            .build();

    return userRepository.save(user);
  }

  private static UserDto mapToUserDto(User user) {
    if (user.getAuthorities() == null) {
      return new UserDto(
          user.getId(), user.getUsername(), Collections.emptyList(), user.getEmail());
    } else {
      return new UserDto(
          user.getId(),
          user.getUsername(),
          user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
          user.getEmail());
    }
  }

  private static UserBatchDto mapToUserBatchDto(User users) {
    return new UserBatchDto(users.getId(), users.getUsername(), users.getEmail());
  }
}
