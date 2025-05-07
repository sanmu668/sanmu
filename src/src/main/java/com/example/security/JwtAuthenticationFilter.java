package com.example.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * @author wzs
 * @date 2025年05月06日 1:01
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = extractToken(request);
            if(token != null){
                Claims claims = jwtUtils.getClaimsFromToken(token);
                setSecurityContext(claims);
            }
        }catch (Exception e){
            logger.error("Error while processing JWT token", e);
        }
        filterChain.doFilter(request,response);
    }

    private void setSecurityContext(Claims claims) {
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
