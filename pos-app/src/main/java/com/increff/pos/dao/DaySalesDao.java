package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class DaySalesDao extends AbstractDao<DaySalesPojo> {
    private static final String GET_ORDERS_BETWEEN_DATES = "SELECT d FROM DaySalesPojo d WHERE d.date BETWEEN :start AND :end ORDER BY d.date";

    public DaySalesDao() {
        super(DaySalesPojo.class);
    }

    public DaySalesPojo getByDate(LocalDate date) {
        return em.find(DaySalesPojo.class, date);
    }

    public List<DaySalesPojo> getBetween(LocalDate start, LocalDate end) {
        return em.createQuery(GET_ORDERS_BETWEEN_DATES, DaySalesPojo.class)
                .setParameter("start", start)
                        .setParameter("end", end)
                                .getResultList();
    }
}
