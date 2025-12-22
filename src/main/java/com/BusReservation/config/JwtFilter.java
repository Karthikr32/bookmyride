package com.BusReservation.config;

import com.BusReservation.constants.Role;
import com.BusReservation.security.JwtService;
import com.BusReservation.security.UserPrincipal;
import com.BusReservation.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        final String token;
        final String subject;

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        subject = jwtService.extractData(token);

        // Process request if token is valid and user is not yet authenticated
        if(subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            assert subject != null;
            UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(subject);

            boolean isValid = jwtService.isTokenValid(token, userPrincipal);
            if(isValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                if(userPrincipal.getRole() == Role.USER) {
                    log.info("Authentication success for the User ID: {}, Mobile: {}", userPrincipal.getId(), userPrincipal.getMobile());
                }
                else log.info("Authentication success for the Management authority ID: {}, Username: {}", userPrincipal.getId(), userPrincipal.getFullName());
            }
        }
        filterChain.doFilter(request, response);
    }


    // Skip JWT filter for specific endpoints
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/public/") || path.startsWith("/auth/bookmyride/public/") || path.startsWith("/auth/bookmyride/management/login");
    }
}


