package com.increff.pos.dao;

import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class ClientDao {

    private static final String SELECT_CLIENT_BY_NAME = "SELECT c FROM ClientPojo c WHERE c.name = :name";
    @PersistenceContext
    private EntityManager em;

    public void insert(ClientPojo p) {
        em.persist(p);
    }

    public ClientPojo getById(Integer id) {
        return em.find(ClientPojo.class, id);
    }

    public ClientPojo getClient(String clientName) {
        TypedQuery<ClientPojo> query = em.createQuery(
                SELECT_CLIENT_BY_NAME, ClientPojo.class);
        query.setParameter("name", clientName);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ClientPojo> selectAll() {
        return em.createQuery("FROM ClientPojo", ClientPojo.class).getResultList();
    }

    public void update(ClientPojo p) {
        em.merge(p);
    }
}
