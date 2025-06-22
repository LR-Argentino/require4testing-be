package org.blackbird.requirefortesting.security.api;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.security.internal.PostgresUserDetailsService;
import org.blackbird.requirefortesting.security.internal.jwt.JwtUtil;
import org.blackbird.requirefortesting.security.model.AuthResponseDto;
import org.blackbird.requirefortesting.security.model.CreateUserDto;
import org.blackbird.requirefortesting.security.model.LoginRequestDto;
import org.blackbird.requirefortesting.security.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final PostgresUserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.username(), loginRequest.password()));
    } catch (BadCredentialsException e) {
      return ResponseEntity.badRequest().body(new AuthResponseDto("Invalid credentials"));
    }

    final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());
    final String jwt = jwtUtil.generateToken(userDetails);

    return ResponseEntity.ok(
        new AuthResponseDto(
            jwt,
            userDetails.getUsername(),
            userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toArray(String[]::new)));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    return ResponseEntity.ok(new AuthResponseDto("Logged out successfully"));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> createUser(@RequestBody CreateUserDto createUserDto) {
    User registerUser = userDetailsService.registerUser(createUserDto);
    String jwt = jwtUtil.generateToken(registerUser);
    return ResponseEntity.ok(
        new AuthResponseDto(
            jwt,
            registerUser.getUsername(),
            registerUser.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toArray(String[]::new)));
  }

  //  @PostMapping("/users/{userId}/roles")
  //  public ResponseEntity<?> assignRole(@PathVariable Long userId, @RequestBody RoleRequest
  // roleRequest) {
  //    List<GrantedAuthority> authorities = roleRequest.getRoles().stream()
  //            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
  //            .collect(Collectors.toList());
  //
  //    userService.updateUserAuthorities(userId, authorities);
  //    return ResponseEntity.ok("Roles assigned successfully");
  //  }
}
