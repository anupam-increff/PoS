package com.increff.pos.dao;

import com.increff.pos.model.data.SalesReportData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class SalesReportDao {

    @PersistenceContext
    private EntityManager em;

    private static final String BASE_SALES_QUERY = 
        "SELECT new com.increff.pos.model.data.SalesReportData(" +
        "c.name, SUM(oi.quantity), SUM(oi.quantity * oi.sellingPrice)) " +
        "FROM OrderPojo o " +
        "JOIN OrderItemPojo oi ON o.id = oi.orderId " +
        "JOIN ProductPojo p ON oi.productId = p.id " +
        "JOIN ClientPojo c ON p.clientId = c.id " +
        "WHERE o.placedAt BETWEEN :start AND :end ";

    private static final String CLIENT_FILTER = "AND LOWER(c.name) LIKE :clientName ";
    private static final String GROUP_ORDER = "GROUP BY c.name ORDER BY SUM(oi.quantity * oi.sellingPrice) DESC";

    private static final String BASE_COUNT_QUERY = 
        "SELECT COUNT(DISTINCT c.name) " +
        "FROM OrderPojo o " +
        "JOIN OrderItemPojo oi ON o.id = oi.orderId " +
        "JOIN ProductPojo p ON oi.productId = p.id " +
        "JOIN ClientPojo c ON p.clientId = c.id " +
        "WHERE o.placedAt BETWEEN :start AND :end ";

    public List<SalesReportData> getSalesReport(ZonedDateTime start, ZonedDateTime end, String clientName, int page, int size) {
        StringBuilder queryBuilder = new StringBuilder(BASE_SALES_QUERY);
        
        if (clientName != null && !clientName.trim().isEmpty()) {
            queryBuilder.append(CLIENT_FILTER);
        }
        
        queryBuilder.append(GROUP_ORDER);

        TypedQuery<SalesReportData> query = em.createQuery(queryBuilder.toString(), SalesReportData.class);
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (clientName != null && !clientName.trim().isEmpty()) {
            query.setParameter("clientName", "%" + clientName.trim().toLowerCase() + "%");
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public Long countTotalClients(ZonedDateTime start, ZonedDateTime end, String clientName) {
        StringBuilder countBuilder = new StringBuilder(BASE_COUNT_QUERY);
        
        if (clientName != null && !clientName.trim().isEmpty()) {
            countBuilder.append(CLIENT_FILTER);
        }

        TypedQuery<Long> query = em.createQuery(countBuilder.toString(), Long.class);
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (clientName != null && !clientName.trim().isEmpty()) {
            query.setParameter("clientName", "%" + clientName.trim().toLowerCase() + "%");
        }

        return query.getSingleResult();
    }
}
