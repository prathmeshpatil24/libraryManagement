package com.library.common.security;

import com.library.common.exception.UserNotFoundException;
import com.library.user.entity.Roles;
import com.library.user.entity.Users;
import com.library.user.repository.RolesRepository;
import com.library.user.repository.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("🔥 OAuth2AuthenticationSuccessHandler HIT 🔥");

       OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
         String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        System.out.println("Authenticated OAuth2 User Email: " + email);
        System.out.println("Authenticated OAuth2 User Name: " + name);
        System.out.println("Authenticated OAuth2 User Provider ID: " + providerId);

        Roles roles = rolesRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new BadRequestException("Role USER not found"));

        Users users = usersRepository.findByEmail(email)
                .orElseGet(
                        ()->{
                            Users newUser = new Users();
                            newUser.setEmail(email);
                            newUser.setName(name);
                            newUser.setProvider("GOOGLE");
                            newUser.setProviderId(providerId); // Add this field if you have it
                            newUser.setPassword(null);
                            newUser.setPasswordSet(false);
                            newUser.setIsActive(true);

                            // Clear and add role properly
                            newUser.getRoles().add(roles);
                            return usersRepository.save(newUser);
                        }
                );


        String accessToken = jwtService.generateAccessToken(users);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
        {
          "token": "%s",
          "email": "%s",
          "passwordSet": "%s",
          "role": "%s",
          "message": "OAuth2 login successful"
        }
        """.formatted(
                accessToken,
                users.getEmail(),
                users.isPasswordSet(),
                users.getRoles()
        ));
    }
}
