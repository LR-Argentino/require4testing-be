package org.blackbird.requirefortesting.security.model;

public record AuthResponseDto(String token, String username, String[] roles, String message) {
  public AuthResponseDto(String message) {
    this(null, null, null, message);
  }

  public AuthResponseDto(String token, String username, String[] roles) {
    this(token, username, roles, null);
  }
}
