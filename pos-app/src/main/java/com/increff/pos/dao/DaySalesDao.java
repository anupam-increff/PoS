package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Transactional
public class DaySalesDao extends AbstractDao<DaySalesPojo> {

    public DaySalesDao() {
        super(DaySalesPojo.class);
    }

    public DaySalesPojo getByDate(ZonedDateTime date) {
        List<DaySalesPojo> result = em.createQuery("SELECT d FROM DaySalesPojo d WHERE DATE(d.reportDate) = DATE(:date)", DaySalesPojo.class)
                .setParameter("date", date)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<DaySalesPojo> getBetween(ZonedDateTime start, ZonedDateTime end) {
        return em.createQuery("SELECT d FROM DaySalesPojo d WHERE d.reportDate BETWEEN :start AND :end ORDER BY d.reportDate DESC", DaySalesPojo.class)
                .setParameter("start", start.toLocalDate().atStartOfDay(start.getZone()))
                .setParameter("end", end.toLocalDate().atTime(23, 59, 59).atZone(end.getZone()))
                .getResultList();
    }
}
