package com.library.common.security;

import com.library.user.entity.Roles;
import com.library.user.entity.Users;
import com.library.user.repository.RolesRepository;
import com.library.user.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;



@AllArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        System.out.println("🔥 CustomOAuth2UserService HIT 🔥");
        OAuth2User oAuth2User = super.loadUser(request);

        try {
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String providerId = oAuth2User.getAttribute("sub");

            // Log for debugging
            System.out.println("OAuth2 User - Email: " + email + ", Name: " + name);

            Roles roles = rolesRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new BadRequestException("Role USER not found"));

            Users users = usersRepository.findByEmail(email).orElse(null);

            if (users == null) {
                Users newUser = new Users();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setProvider("GOOGLE");
                newUser.setProviderId(providerId); // Add this field if you have it
                newUser.setPassword(null);
                newUser.setPasswordSet(false);
                newUser.setIsActive(true);

                // Clear and add role properly
//                newUser.getRoles().clear();
                newUser.getRoles().add(roles);

                users = usersRepository.save(newUser);
                System.out.println("New user created: " + users.getEmail());
            } else {
                System.out.println("Existing user found: " + users.getEmail());
            }

            return oAuth2User;

        } catch (Exception e) {
            System.err.println("Error in OAuth2UserService: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("User processing failed: " + e.getMessage());
        }
    }
}
