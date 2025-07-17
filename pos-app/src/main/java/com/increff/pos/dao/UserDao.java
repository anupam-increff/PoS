package com.increff.pos.dao;

import com.increff.pos.pojo.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserDao extends AbstractDao<UserPojo> {

    private static final String SELECT_BY_EMAIL = "SELECT u FROM UserPojo u WHERE u.email = :email";

    public UserDao() {
        super(UserPojo.class);
    }

    public UserPojo findByEmail(String email) {
        try {
            TypedQuery<UserPojo> query = em.createQuery(SELECT_BY_EMAIL, UserPojo.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
} 