package org.blackbird.requirefortesting.security.internal.error;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.blackbird.requirefortesting.security.model.AuthResponseDto;
import org.blackbird.requirefortesting.shared.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<AuthResponseDto> handleBadCredentials(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new AuthResponseDto("Invalid credentials"));
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<AuthResponseDto> handleUserNotFound(UsernameNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new AuthResponseDto("User not found"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<AuthResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponseDto(ex.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    LocalDateTime now = LocalDateTime.now();
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .timestamp(now)
            .status(403)
            .error("ACCESS_DENIED")
            .message("Sie haben nicht die erforderlichen Berechtigungen für diese Aktion")
            .path(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }
}
