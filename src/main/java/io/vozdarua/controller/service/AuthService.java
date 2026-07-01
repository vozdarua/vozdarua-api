package io.vozdarua.controller.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import io.vozdarua.model.dto.AuthResponse;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Transactional
    public void signupUser(User user) {
        user.password = BcryptUtil.bcryptHash(user.password);
        user.role = Roles.USER;
        user.persist();
    }

    public AuthResponse token(User user) {
        long currentTimeInSecs = System.currentTimeMillis() / 1000;

        String token = Jwt.issuer(issuer)
                .upn(user.email)
                .groups(user.role)
                .claim(Claims.phone_number, user.phone)
                .expiresAt(currentTimeInSecs + 3600) // Expira em 1 hora
                .sign();

        return new AuthResponse(token);
    }
}
