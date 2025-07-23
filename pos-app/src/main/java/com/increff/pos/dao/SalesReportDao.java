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

    private static final String ORDER_TO_CLIENT_JOINS =
            "FROM OrderPojo o " +
                    "JOIN OrderItemPojo oi ON o.id = oi.orderId " +
                    "JOIN ProductPojo p ON oi.productId = p.id " +
                    "JOIN ClientPojo c ON p.clientId = c.id ";

    private static final String DATE_RANGE_FILTER = "WHERE o.createdAt BETWEEN :start AND :end ";
    private static final String CLIENT_NAME_FILTER = "AND LOWER(c.name) LIKE :clientName ";

    private static final String CLIENT_SALES_DATA_SELECT =
            "SELECT new com.increff.pos.model.data.SalesReportData(" +
                    "c.name, SUM(oi.quantity), SUM(oi.quantity * oi.sellingPrice)) ";

    private static final String DISTINCT_CLIENT_COUNT_SELECT = "SELECT COUNT(DISTINCT c.name) ";
    private static final String GROUP_BY_CLIENT_ORDER_BY_REVENUE = "GROUP BY c.name ORDER BY SUM(oi.quantity * oi.sellingPrice) DESC";

    public List<SalesReportData> getSalesReport(ZonedDateTime start, ZonedDateTime end, String clientName, int page, int size) {
        String query = buildSalesReportQuery(clientName);
        TypedQuery<SalesReportData> typedQuery = em.createQuery(query, SalesReportData.class);

        setParameters(typedQuery, start, end, clientName);
        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    public Long countTotalClients(ZonedDateTime start, ZonedDateTime end, String clientName) {
        String query = buildClientCountQuery(clientName);
        TypedQuery<Long> typedQuery = em.createQuery(query, Long.class);

        setParameters(typedQuery, start, end, clientName);
        return typedQuery.getSingleResult();
    }

    private String buildSalesReportQuery(String clientName) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(CLIENT_SALES_DATA_SELECT)
                    .append(ORDER_TO_CLIENT_JOINS)
                    .append(DATE_RANGE_FILTER);

        if (hasClientFilter(clientName)) {
            queryBuilder.append(CLIENT_NAME_FILTER);
        }

        queryBuilder.append(GROUP_BY_CLIENT_ORDER_BY_REVENUE);
        return queryBuilder.toString();
    }

    private String buildClientCountQuery(String clientName) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(DISTINCT_CLIENT_COUNT_SELECT)
                    .append(ORDER_TO_CLIENT_JOINS)
                    .append(DATE_RANGE_FILTER);

        if (hasClientFilter(clientName)) {
            queryBuilder.append(CLIENT_NAME_FILTER);
        }

        queryBuilder.append("GROUP BY c.name");
        return queryBuilder.toString();
    }

    private void setParameters(TypedQuery<?> query, ZonedDateTime start, ZonedDateTime end, String clientName) {
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (hasClientFilter(clientName)) {
            query.setParameter("clientName", "%" + clientName.trim().toLowerCase() + "%");
        }
    }

    private boolean hasClientFilter(String clientName) {
        return clientName != null && !clientName.trim().isEmpty();
    }
}