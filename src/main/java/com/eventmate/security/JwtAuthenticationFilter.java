package com.eventmate.security;

import com.eventmate.entity.User;
import com.eventmate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {
            if (!jwtService.isValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            String email = jwtService.extractEmail(token);
            User user = userService.getByEmail(email);
            // set principal as user ID (Long) so controllers can get numeric id
            var authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
            var auth = new UsernamePasswordAuthenticationToken(user.getId(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (RuntimeException ex) {
            // in case of invalid token, do nothing (request will be treated as unauthenticated)
        }

        filterChain.doFilter(request, response);
    }
}
