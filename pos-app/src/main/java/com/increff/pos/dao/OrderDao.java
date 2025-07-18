package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class OrderDao extends AbstractDao<OrderPojo> {

    private static final String SELECT_ALL = "SELECT o FROM OrderPojo o ORDER BY o.time DESC";
    private static final String SEARCH_BASE = "SELECT o FROM OrderPojo o WHERE o.time BETWEEN :start AND :end";
    private static final String COUNT_BASE = "SELECT COUNT(o) FROM OrderPojo o WHERE o.time BETWEEN :start AND :end";
    private static final String SELECT_BY_DATE = "SELECT o FROM OrderPojo o WHERE o.time >= :startOfDay AND o.time < :endOfDay ORDER BY o.time DESC";

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> search(ZonedDateTime start, ZonedDateTime end, Boolean invoiceGenerated, String query, int page, int size) {
        String jpql = buildQuery(false, start, end, invoiceGenerated, query);
        Map<String, Object> params = buildParams(start, end, query);
        return getPaginatedResults(jpql, page, size, params);
    }

    public long countMatching(ZonedDateTime start, ZonedDateTime end, Boolean invoiceGenerated, String query) {
        String jpql = buildQuery(true, start, end, invoiceGenerated, query);
        Map<String, Object> params = buildParams(start, end, query);
        TypedQuery<Long> countQuery = em.createQuery(jpql, Long.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return countQuery.getSingleResult();
    }

    public List<OrderPojo> getByDate(ZonedDateTime date) {
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(date.getZone());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);
        
        return em.createQuery(SELECT_BY_DATE, OrderPojo.class)
                .setParameter("startOfDay", startOfDay)
                .setParameter("endOfDay", endOfDay)
                .getResultList();
    }

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return getPaginatedResults(SELECT_ALL, page, size, null);
    }

    private String buildQuery(boolean isCount, ZonedDateTime start, ZonedDateTime end, Boolean invoiceGenerated, String query) {
        StringBuilder jpql = new StringBuilder(isCount ? COUNT_BASE : SEARCH_BASE);
        
        if (invoiceGenerated != null) {
            if (invoiceGenerated) {
                jpql.append(" AND o.invoicePath IS NOT NULL");
            } else {
                jpql.append(" AND o.invoicePath IS NULL");
            }
        }
        
        if (query != null && !query.trim().isEmpty()) {
            // Search by order ID as string conversion
            jpql.append(" AND CAST(o.id AS string) LIKE :query");
        }
        
        if (!isCount) {
            jpql.append(" ORDER BY o.time DESC");
        }
        
        return jpql.toString();
    }

    private Map<String, Object> buildParams(ZonedDateTime start, ZonedDateTime end, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        
        if (query != null && !query.trim().isEmpty()) {
            params.put("query", "%" + query.toLowerCase() + "%");
        }
        
        return params;
    }
}
