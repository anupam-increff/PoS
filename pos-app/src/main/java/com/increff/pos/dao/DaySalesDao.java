package com.increff.pos.dao;

import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DaySalesDao extends AbstractDao<DaySalesPojo> {

    private static final String SELECT_BY_DATE_RANGE = "SELECT d FROM DaySalesPojo d WHERE d.reportDate >= :startOfDay AND d.reportDate <= :endOfDay";
    private static final String SELECT_BETWEEN_DATES = "SELECT d FROM DaySalesPojo d WHERE d.reportDate BETWEEN :start AND :end ORDER BY d.reportDate DESC";

    public DaySalesDao() {
        super(DaySalesPojo.class);
    }

    public DaySalesPojo getReportForDate(ZonedDateTime date) {
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(date.getZone());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        Map<String, Object> params = new HashMap<>();
        params.put("startOfDay", startOfDay);
        params.put("endOfDay", endOfDay);

        return getSingleResultOrNull(SELECT_BY_DATE_RANGE, params);
    }

    public List<DaySalesPojo> getReportBetweenDates(ZonedDateTime start, ZonedDateTime end) {
        return em.createQuery(SELECT_BETWEEN_DATES, DaySalesPojo.class).setParameter("start", start).setParameter("end", end).getResultList();
    }
}
