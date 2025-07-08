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

    public List<SalesReportData> getSalesReport(ZonedDateTime start, ZonedDateTime end, String clientName, int page, int size) {

        String baseQuery = "SELECT new com.increff.pos.model.data.SalesReportData(" + "c.name, SUM(oi.quantity), SUM(oi.quantity * oi.sellingPrice)) " + "FROM com.increff.pos.pojo.OrderPojo o " + "JOIN com.increff.pos.pojo.OrderItemPojo oi ON o.id = oi.orderId " + "JOIN com.increff.pos.pojo.ProductPojo p ON oi.productId = p.id " + "JOIN com.increff.pos.pojo.ClientPojo c ON p.clientId = c.id " + "WHERE o.time BETWEEN :start AND :end ";

        if (clientName != null && !clientName.trim().isEmpty()) {
            baseQuery += "AND LOWER(c.name) LIKE :clientName ";
        }

        baseQuery += "GROUP BY c.name ORDER BY SUM(oi.quantity * oi.sellingPrice) DESC";

        TypedQuery<SalesReportData> query = em.createQuery(baseQuery, SalesReportData.class);
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
        String baseCount = "SELECT COUNT(DISTINCT c.name) " + "FROM com.increff.pos.pojo.OrderPojo o " + "JOIN com.increff.pos.pojo.OrderItemPojo oi ON o.id = oi.orderId " + "JOIN com.increff.pos.pojo.ProductPojo p ON oi.productId = p.id " + "JOIN com.increff.pos.pojo.ClientPojo c ON p.clientId = c.id " + "WHERE o.time BETWEEN :start AND :end ";

        if (clientName != null && !clientName.trim().isEmpty()) {
            baseCount += "AND LOWER(c.name) LIKE :clientName ";
        }

        TypedQuery<Long> query = em.createQuery(baseCount, Long.class);
        query.setParameter("start", start);
        query.setParameter("end", end);

        if (clientName != null && !clientName.trim().isEmpty()) {
            query.setParameter("clientName", "%" + clientName.trim().toLowerCase() + "%");
        }

        return query.getSingleResult();
    }
}
