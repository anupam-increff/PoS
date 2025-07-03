package com.increff.pos.dao;

import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class ClientDao extends AbstractDao<ClientPojo> {

    private static final String SELECT_CLIENT_BY_NAME = "SELECT c FROM ClientPojo c WHERE c.name = :name";

    public ClientDao() {
        super(ClientPojo.class);
    }

    public ClientPojo getClientByName(String clientName) {
        return em.createQuery(SELECT_CLIENT_BY_NAME, ClientPojo.class)
                .setParameter("name", clientName)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
}
