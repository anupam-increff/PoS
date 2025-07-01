package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    public List<OrderPojo> selectByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);

        String jpql = "SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end";
        TypedQuery<OrderPojo> query = em.createQuery(jpql, OrderPojo.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    public void update(OrderPojo p) {
        em.merge(p);
    }
}
