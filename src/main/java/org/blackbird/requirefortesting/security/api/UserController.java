package org.blackbird.requirefortesting.security.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.security.internal.PostgresUserDetailsService;
import org.blackbird.requirefortesting.security.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
  private final PostgresUserDetailsService userDetailsService;

  @GetMapping
  public ResponseEntity<List<UserDto>> getAllUsers() {
    List<UserDto> users = userDetailsService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @PostMapping("/batch")
  public ResponseEntity<Map<Long, UserDto>> getUsersByIds(@RequestBody Set<Long> userIds) {
    Map<Long, UserDto> users = userDetailsService.getUsersByIds(userIds);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
    UserDto user = userDetailsService.getUserById(userId);
    return ResponseEntity.ok(user);
  }
}
