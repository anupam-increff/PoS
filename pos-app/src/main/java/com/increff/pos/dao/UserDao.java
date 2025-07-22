package com.increff.pos.dao;

import com.increff.pos.pojo.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao extends AbstractDao<UserPojo> {
    private static final String SELECT_BY_EMAIL = "SELECT u FROM UserPojo u WHERE u.email = :email";

    public UserDao() {
        super(UserPojo.class);
    }

    public UserPojo findByEmail(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        return getSingleResultOrNull(SELECT_BY_EMAIL, params);
    }
}