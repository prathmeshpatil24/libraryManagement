package com.library.common.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;


    @Autowired
    private CustomUserDetailService customUserDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authheader = request.getHeader("Authorization");
        String token = null;
        String userName = null;


        //String token = authHeader.replace("Bearer ", "").trim();
        // Example: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        if(authheader != null && authheader.startsWith("Bearer ")) {
            token = authheader.replace("Bearer ", "").trim(); // Use trim to remove accidental spaces

            try {
                userName = jwtService.extractUserName(token);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired JWT token");
                return;
            }
        }


        if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {

            // Load user details from DB
            UserDetails userDetails = customUserDetailService
                    .loadUserByUsername(userName);

            if(jwtService.validateToken(token,userDetails)) {

                // Create authentication object
                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set request details (e.g., IP, session info)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                );

                // Set authentication in Spring Security context
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
            }
        }

        // 5. Continue the request
        // Let Spring continue to next filter/controller
        filterChain.doFilter(request, response);
    }
}
