package com.lightningbid.common.config;

import com.lightningbid.auth.dto.CustomOAuth2User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

    @NonNull
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(CustomOAuth2User.class::isInstance)
                .map(CustomOAuth2User.class::cast)
                .map(CustomOAuth2User::getId);
    }
}
