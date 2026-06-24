package io.vozdarua.controller.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.vozdarua.model.entity.Roles;
import io.vozdarua.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.Objects;

@ApplicationScoped
public class AccountService {

    @Transactional
    public void signupUser(User user) {
        user.password = BcryptUtil.bcryptHash(user.password);
        user.role = Roles.USER;
        user.persist();
    }
}
