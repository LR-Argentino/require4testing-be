package org.blackbird.requirefortesting.security.api;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.security.internal.JwtUtil;
import org.blackbird.requirefortesting.security.internal.PostgresUserDetailsService;
import org.blackbird.requirefortesting.security.model.AuthResponseDto;
import org.blackbird.requirefortesting.security.model.CreateUserDto;
import org.blackbird.requirefortesting.security.model.LoginRequestDto;
import org.blackbird.requirefortesting.security.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final PostgresUserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

    final User user = (User) userDetailsService.loadUserByUsername(loginRequest.username());
    final String jwt = jwtUtil.generateToken(user);

    return ResponseEntity.ok(createSuccessResponse(jwt, user));
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> createUser(@RequestBody CreateUserDto createUserDto) {
    User registerUser = userDetailsService.registerUser(createUserDto);
    String jwt = jwtUtil.generateToken(registerUser);
    return ResponseEntity.ok(createSuccessResponse(jwt, registerUser));
  }

  @PostMapping("/logout")
  public ResponseEntity<AuthResponseDto> logout() {
    return ResponseEntity.ok(new AuthResponseDto("Logged out successfully"));
  }

  private AuthResponseDto createSuccessResponse(String jwt, User user) {
    return new AuthResponseDto(
        jwt,
        user.getUsername(),
        user.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .toArray(String[]::new));
  }
}
