package br.com.jmarcos.assessment_task.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
    @Value("${assessment_task.jwt.expiration}")
    private String expiration;

    @Value("${assessment_task.jwt.secret}")
    private String secret;

    public String createToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date today = new Date();
        Date deadline = new Date(today.getTime() + Long.parseLong(expiration));
        return Jwts.builder()
                .setIssuer("assessment task API")
                .setSubject(user.getId().toString())
                .setIssuedAt(today)
                .setExpiration(deadline)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isAValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }
}
