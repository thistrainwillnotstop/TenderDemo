package com.intv.tender.tenderapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    private String SECRET_KEY = "secret";

    public String extractUsername(String token) throws EJwtTokenParsingError {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws EJwtTokenParsingError {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws EJwtTokenParsingError {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token) throws EJwtTokenParsingError {

        Claims claims = null;
        try
        {
            claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        }
        catch (Exception e)
        {
            throw new EJwtTokenParsingError(e.getMessage());
        }

        return claims;
    }

    private Boolean isTokenExpired(String token) throws EJwtTokenParsingError {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails myUserDetails) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> grantedAuthorities = myUserDetails.getAuthorities();

        if(grantedAuthorities != null)
        {
            claims.put("authorities",
                        grantedAuthorities.stream().map(Object::toString).collect(Collectors.joining(",")) );
        }

        return createToken(claims, myUserDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }


    public static class EJwtTokenParsingError extends Exception {
        public EJwtTokenParsingError(String msg) { super(msg); }
    }

}