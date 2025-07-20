package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {

    private static final String SELECT_ALL = "SELECT o FROM OrderPojo o ORDER BY o.placedAt DESC";
    private static final String SEARCH_BASE = "SELECT o FROM OrderPojo o WHERE o.placedAt BETWEEN :start AND :end";
    private static final String COUNT_BASE = "SELECT COUNT(o) FROM OrderPojo o WHERE o.placedAt BETWEEN :start AND :end";
    private static final String SELECT_BY_DATE = "SELECT o FROM OrderPojo o WHERE o.placedAt >= :startOfDay AND o.placedAt < :endOfDay ORDER BY o.placedAt DESC";

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> searchOrder(ZonedDateTime start, ZonedDateTime end, Boolean invoiceGenerated, String query, int page, int size) {
        String jpql = buildQuery(false, start, end, invoiceGenerated, query);
        Map<String, Object> params = buildParams(start, end, query);
        return getPaginatedResults(jpql, page, size, params);
    }

    public long countMatchingOrders(ZonedDateTime start, ZonedDateTime end, Boolean invoiceGenerated, String query) {
        String jpql = buildQuery(true, start, end, invoiceGenerated, query);
        Map<String, Object> params = buildParams(start, end, query);
        TypedQuery<Long> countQuery = em.createQuery(jpql, Long.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return countQuery.getSingleResult();
    }

    public List<OrderPojo> getOrdersForDate(ZonedDateTime date) {
        ZonedDateTime startOfDay = date.toLocalDate().atStartOfDay(date.getZone());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);
        
        return em.createQuery(SELECT_BY_DATE, OrderPojo.class)
                .setParameter("startOfDay", startOfDay)
                .setParameter("endOfDay", endOfDay)
                .getResultList();
    }

    public List<OrderPojo> getAllOrders(int page, int size) {
        return getPaginatedResults(SELECT_ALL, page, size, null);
    }

    private String buildQuery(boolean isCount, ZonedDateTime start, ZonedDateTime end, Boolean invoiceGenerated, String query) {
        StringBuilder jpql = new StringBuilder(isCount ? COUNT_BASE : SEARCH_BASE);
        
        if (invoiceGenerated != null) {
            if (invoiceGenerated) {
                jpql.append(" AND o.invoiceGenerated = true");
            } else {
                jpql.append(" AND o.invoiceGenerated = false");
            }
        }
        
        if (query != null && !query.trim().isEmpty()) {
            // Search by order ID as string conversion
            jpql.append(" AND CAST(o.id AS string) LIKE :query");
        }
        
        if (!isCount) {
            jpql.append(" ORDER BY o.placedAt DESC");
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
