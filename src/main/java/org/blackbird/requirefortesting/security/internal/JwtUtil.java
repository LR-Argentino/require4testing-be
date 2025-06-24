package org.blackbird.requirefortesting.security.internal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.blackbird.requirefortesting.security.model.User;
import org.blackbird.requirefortesting.shared.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil implements JwtService {

  @Value("${jwt.secret:mySecretKey1234567890123456789012345678901234567890}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}")
  private int jwtExpiration;

  private static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";
  private static final Integer INDEX_OF_TOKEN_STARTS = 7;

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  @Override
  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    String authToken = extractTokenFromHeader(token);
    final Claims claims = extractAllClaims(authToken);
    return claimsResolver.apply(claims);
  }

  private String extractTokenFromHeader(String authToken) {
    if (authToken != null && authToken.startsWith(AUTHORIZATION_BEARER_PREFIX)) {
      return authToken.substring(INDEX_OF_TOKEN_STARTS);
    }
    return authToken;
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(
        "roles",
        userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).toArray());

    if (userDetails instanceof User) {
      User user = (User) userDetails;
      claims.put("userId", user.getId());
    }

    return createToken(claims, userDetails.getUsername());
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSigningKey())
        .compact();
  }

  @Override
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
