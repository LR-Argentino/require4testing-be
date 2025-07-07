package org.blackbird.requirefortesting.security.model;

public record AuthResponseDto(
    String token, String username, String[] roles, String email, String message) {
  public AuthResponseDto(String message) {
    this(null, null, null, null, message);
  }

  public AuthResponseDto(String token, String username, String email, String[] roles) {
    this(token, username, roles, email, null);
  }
}
