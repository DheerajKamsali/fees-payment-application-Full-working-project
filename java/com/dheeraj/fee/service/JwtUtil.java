package com.dheeraj.fee.service;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	

private final String SECRET = "mysecretkeymysecretkeymysecretkey";

public String generateToken(String email) {

return Jwts.builder()
.setSubject(email)
.setIssuedAt(new Date())
.setExpiration(new Date(System.currentTimeMillis() + 86400000))
.signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
.compact();
}

public String extractEmail(String token) {

return Jwts.parserBuilder()
.setSigningKey(SECRET.getBytes())
.build()
.parseClaimsJws(token)
.getBody()
.getSubject();
}

}
