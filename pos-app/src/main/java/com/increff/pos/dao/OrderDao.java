package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Repository
@Transactional
public class OrderDao extends AbstractDao<OrderPojo> {

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return getPaginatedResults("SELECT o FROM OrderPojo o ORDER BY o.time DESC", page, size, null);
    }

    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query, int page, int size) {
        String jpql = buildQuery(false, start, end, invoiceGenerated, query);
        Map<String, Object> params = buildParams(start, end, query);
        return getPaginatedResults(jpql, page, size, params);
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        String jpql = buildQuery(true, start, end, invoiceGenerated, query);
        Map<String, Object> params = buildParams(start, end, query);
        TypedQuery<Long> countQuery = em.createQuery(jpql, Long.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return countQuery.getSingleResult();
    }

    public List<OrderPojo> getByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);
        return em.createQuery("SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end ORDER BY o.time DESC", OrderPojo.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // --- Dynamic JPQL builder ---

    private String buildQuery(boolean isCount, LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        StringBuilder sb = new StringBuilder();
        sb.append(isCount ? "SELECT COUNT(o) FROM OrderPojo o WHERE 1=1" : "SELECT o FROM OrderPojo o WHERE 1=1");

        if (start != null && end != null) {
            sb.append(" AND o.time >= :start AND o.time < :end");
        }

        if (invoiceGenerated != null) {
            sb.append(invoiceGenerated
                    ? " AND o.invoicePath IS NOT NULL"
                    : " AND o.invoicePath IS NULL");
        }

        if (query != null && !query.trim().isEmpty()) {
            sb.append(" AND STR(o.id) LIKE :query");
        }

        if (!isCount) {
            sb.append(" ORDER BY o.time DESC");
        }

        return sb.toString();
    }

    private Map<String, Object> buildParams(LocalDate start, LocalDate end, String query) {
        Map<String, Object> params = new HashMap<>();

        if (start != null && end != null) {
            ZonedDateTime startZdt = start.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime endZdt = end.plusDays(1).atStartOfDay(ZoneOffset.UTC);
            params.put("start", startZdt);
            params.put("end", endZdt);
        }

        if (query != null && !query.trim().isEmpty()) {
            params.put("query", "%" + query.trim() + "%");
        }

        return params;
    }
}
