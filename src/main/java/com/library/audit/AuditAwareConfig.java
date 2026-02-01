package com.library.audit;


import com.library.common.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareConfig implements AuditorAware<Long> {
//    @Override
//    public Optional<Long> getCurrentAuditor() {
//
//        Authentication auth = SecurityContextHolder
//                .getContext()
//                .getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated()
//                || auth.getPrincipal().equals("anonymousUser")) {
//            return Optional.empty();
//        }
//        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
//        return Optional.of(user.getUserId());
//
//       // return Optional.of(0); // hardcode admin/user id  as 2/0  for dev
//    }

    @Override
    public Optional<Long> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // ✅ Local login
        if (principal instanceof CustomUserDetails customUser) {
            return Optional.of(customUser.getUserId());
        }

        // ✅ OAuth2 / Google login (OIDC)
        if (principal instanceof DefaultOidcUser oidcUser) {
            String email = oidcUser.getAttribute("email");
            // Option 1: return null
            // Option 2 (better): lookup user ID by email
            return Optional.empty();
        }

        // ✅ OAuth2 (non-OIDC)
        if (principal instanceof DefaultOAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            return Optional.empty();
        }

        return Optional.empty();
    }
}
