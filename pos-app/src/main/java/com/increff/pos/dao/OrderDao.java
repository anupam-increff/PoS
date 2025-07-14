package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(rollbackFor = ApiException.class)
public class OrderDao extends AbstractDao<OrderPojo> {

    private static final String SELECT_ALL_PAGINATED = "SELECT o FROM OrderPojo o ORDER BY o.time DESC";
    private static final String SELECT_BY_DATE_RANGE = "SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end ORDER BY o.time DESC";
    private static final String COUNT_BY_DATE_RANGE = "SELECT COUNT(o) FROM OrderPojo o WHERE o.time >= :start AND o.time < :end";
    private static final String SELECT_BY_INVOICE_STATUS = "SELECT o FROM OrderPojo o WHERE o.invoicePath IS NULL ORDER BY o.time DESC";
    private static final String SELECT_BY_INVOICE_STATUS_GENERATED = "SELECT o FROM OrderPojo o WHERE o.invoicePath IS NOT NULL ORDER BY o.time DESC";
    private static final String COUNT_BY_INVOICE_STATUS = "SELECT COUNT(o) FROM OrderPojo o WHERE o.invoicePath IS NULL";
    private static final String COUNT_BY_INVOICE_STATUS_GENERATED = "SELECT COUNT(o) FROM OrderPojo o WHERE o.invoicePath IS NOT NULL";
    private static final String SELECT_BY_DATE_AND_INVOICE_STATUS = "SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end AND o.invoicePath IS NULL ORDER BY o.time DESC";
    private static final String SELECT_BY_DATE_AND_INVOICE_STATUS_GENERATED = "SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end AND o.invoicePath IS NOT NULL ORDER BY o.time DESC";
    private static final String COUNT_BY_DATE_AND_INVOICE_STATUS = "SELECT COUNT(o) FROM OrderPojo o WHERE o.time >= :start AND o.time < :end AND o.invoicePath IS NULL";
    private static final String COUNT_BY_DATE_AND_INVOICE_STATUS_GENERATED = "SELECT COUNT(o) FROM OrderPojo o WHERE o.time >= :start AND o.time < :end AND o.invoicePath IS NOT NULL";

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return getPaginatedResults(SELECT_ALL_PAGINATED, page, size, null);
    }

    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query, int page, int size) {
        String jpql = getSearchQuery(start, end, invoiceGenerated);
        Map<String, Object> params = buildSearchParams(start, end, invoiceGenerated);
        return getPaginatedResults(jpql, page, size, params);
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        String jpql = getCountQuery(start, end, invoiceGenerated);
        Map<String, Object> params = buildSearchParams(start, end, invoiceGenerated);
        
        TypedQuery<Long> countQuery = em.createQuery(jpql, Long.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return countQuery.getSingleResult();
    }

    public List<OrderPojo> getByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);
        return em.createQuery(SELECT_BY_DATE_RANGE, OrderPojo.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // --- Private Utility Methods ---

    private String getSearchQuery(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        if (start != null && end != null && invoiceGenerated != null) {
            return invoiceGenerated ? SELECT_BY_DATE_AND_INVOICE_STATUS_GENERATED : SELECT_BY_DATE_AND_INVOICE_STATUS;
        } else if (start != null && end != null) {
            return SELECT_BY_DATE_RANGE;
        } else if (invoiceGenerated != null) {
            return invoiceGenerated ? SELECT_BY_INVOICE_STATUS_GENERATED : SELECT_BY_INVOICE_STATUS;
        } else {
            return SELECT_ALL_PAGINATED;
        }
    }

    private String getCountQuery(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        if (start != null && end != null && invoiceGenerated != null) {
            return invoiceGenerated ? COUNT_BY_DATE_AND_INVOICE_STATUS_GENERATED : COUNT_BY_DATE_AND_INVOICE_STATUS;
        } else if (start != null && end != null) {
            return COUNT_BY_DATE_RANGE;
        } else if (invoiceGenerated != null) {
            return invoiceGenerated ? COUNT_BY_INVOICE_STATUS_GENERATED : COUNT_BY_INVOICE_STATUS;
        } else {
            return "SELECT COUNT(o) FROM OrderPojo o";
        }
    }

    private Map<String, Object> buildSearchParams(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        Map<String, Object> params = new HashMap<>();
        
        if (start != null && end != null) {
            ZonedDateTime startZdt = start.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime endZdt = end.plusDays(1).atStartOfDay(ZoneOffset.UTC);
            params.put("start", startZdt);
            params.put("end", endZdt);
        }
        
        return params;
    }
}
