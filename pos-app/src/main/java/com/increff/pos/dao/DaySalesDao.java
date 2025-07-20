package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class DaySalesDao extends AbstractDao<DaySalesPojo> {

    public DaySalesDao() {
        super(DaySalesPojo.class);
    }

    public DaySalesPojo getReportForDate(ZonedDateTime date) {
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(date.getZone());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);
        
        List<DaySalesPojo> result = em.createQuery("SELECT d FROM DaySalesPojo d WHERE d.reportDate >= :startOfDay AND d.reportDate <=:endOfDay", DaySalesPojo.class)
                .setParameter("startOfDay", startOfDay)
                .setParameter("endOfDay", endOfDay)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<DaySalesPojo> getReportBetweenDates(ZonedDateTime start, ZonedDateTime end) {
        return em.createQuery("SELECT d FROM DaySalesPojo d WHERE d.reportDate BETWEEN :start AND :end ORDER BY d.reportDate DESC", DaySalesPojo.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
