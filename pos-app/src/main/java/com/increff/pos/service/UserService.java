package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.model.enums.UserRole;
import com.increff.pos.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@Service
@Transactional(rollbackFor = ApiException.class)
public class UserService {

    @Autowired
    private UserDao userDao;

    @Value("${auth.supervisor.emails}")
    private String supervisorEmails;

    @Transactional
    public UserPojo signup(String email, String password) {
        if (!Objects.isNull(userDao.findByEmail(email))) {
            throw new ApiException("User with email " + email + " already exists");
        }

        UserRole role = isSupervisorEmail(email) ? UserRole.SUPERVISOR : UserRole.OPERATOR;
        UserPojo user = new UserPojo(email, password, role);
        userDao.insert(user);
        return user;
    }

    @Transactional(readOnly = true)
    public UserPojo login(String email, String password) {
        UserPojo user = userDao.findByEmail(email);
        if (Objects.isNull(user) || !password.equals(user.getPasswordHash())) {
            throw new ApiException("Invalid email or password combination");
        }
        return user;
    }

    private boolean isSupervisorEmail(String email) {
        if (Objects.isNull(supervisorEmails) || supervisorEmails.trim().isEmpty()) {
            return false;
        }
        return Arrays.stream(supervisorEmails.split(",")).map(String::trim).anyMatch(supervisorEmail -> supervisorEmail.equalsIgnoreCase(email));
    }
}
