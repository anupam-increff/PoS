package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class OrderDao {

    @PersistenceContext
    private EntityManager em;

    public void insert(OrderPojo p) {
        em.persist(p);
    }

    public OrderPojo select(Integer id) {
        return em.find(OrderPojo.class, id);
    }

    public List<OrderPojo> selectAll() {
        return em.createQuery("FROM OrderPojo", OrderPojo.class).getResultList();
    }
    public void update(OrderPojo p) {
        em.merge(p);
    }
}
