package com.capstone.arfly.common.auth;


import com.capstone.arfly.common.exception.EmptyTokenException;
import com.capstone.arfly.common.exception.InvalidHeaderException;
import com.capstone.arfly.common.exception.InvalidTokenException;
import com.capstone.arfly.common.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//Access Token Authentication Filter
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final SecretKey ACCESS_SECRET_KEY;

    public JwtTokenFilter(@Value("${jwt.access-secret}") String accessSecretKey) {
        this.ACCESS_SECRET_KEY = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(accessSecretKey));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        String token = request.getHeader("Authorization");
        try {
            if (token != null && !token.isBlank()) {
                if (!token.startsWith("Bearer ")) {
                    throw new InvalidHeaderException();
                }
                //토큰 추출
                String jwtToken = token.substring(7);
                if (jwtToken.isBlank()) {
                    throw new EmptyTokenException();
                }
                //토큰 유효성 및 기간 검증
                Claims claims = Jwts.parser().verifyWith(ACCESS_SECRET_KEY).build().parseSignedClaims(jwtToken)
                        .getPayload();

                //Authentication 생성 후 Security Context에 주입
                List<GrantedAuthority> authorities = new ArrayList<>();
                String role = claims.get("role", String.class);
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken,
                        userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            throw new TokenExpiredException();
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            throw new InvalidTokenException();
        } catch (Exception e) {
            log.warn(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(e.getMessage());
            SecurityContextHolder.clearContext();
        }

    }
}
