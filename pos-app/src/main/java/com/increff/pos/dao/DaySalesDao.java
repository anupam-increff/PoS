package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DaySalesDao {

    @PersistenceContext
    private EntityManager em;

    public void insert(DaySalesPojo pojo) {
        em.persist(pojo);
    }

    public DaySalesPojo getByDate(LocalDate date) {
        return em.find(DaySalesPojo.class, date);
    }

    public List<DaySalesPojo> getBetween(LocalDate start, LocalDate end) {
        String jpql = "SELECT d FROM DaySalesPojo d WHERE d.date BETWEEN :start AND :end ORDER BY d.date";
        TypedQuery<DaySalesPojo> query = em.createQuery(jpql, DaySalesPojo.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }
}
