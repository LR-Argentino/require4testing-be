package org.blackbird.requirefortesting.shared;

import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
  String extractUsername(String token);

  Long extractUserId(String token);

  Date extractExpiration(String token);

  String generateToken(UserDetails userDetails);

  Boolean validateToken(String token, UserDetails userDetails);

  <T> T extractClaim(
      String token, java.util.function.Function<io.jsonwebtoken.Claims, T> claimsResolver);
}
