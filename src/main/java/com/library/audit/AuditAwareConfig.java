package com.library.audit;


import com.library.common.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareConfig implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return Optional.of(user.getUserId());

       // return Optional.of(0); // hardcode admin/user id  as 2/0  for dev
    }
}
