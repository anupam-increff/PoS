package com.increff.pos.dao;

import com.increff.pos.model.form.OrderSearchForm;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Transactional
public class OrderDao extends AbstractDao<OrderPojo> {


    private static final String SELECT_ALL_PAGINATED = "SELECT o FROM OrderPojo o ORDER BY o.time DESC";
    private static final String COUNT_ALL = "SELECT COUNT(o) FROM OrderPojo o";
    

    private static final String BASE_WHERE_TIME_RANGE = "o.time >= :start AND o.time < :end";
    private static final String BASE_ORDER_BY = " ORDER BY o.time DESC";

    private static final String SELECT_BASE = "SELECT o FROM OrderPojo o WHERE ";
    private static final String COUNT_BASE = "SELECT COUNT(o) FROM OrderPojo o WHERE ";

    private static final String INVOICE_GENERATED = " AND o.invoicePath IS NOT NULL";
    private static final String INVOICE_NOT_GENERATED = " AND o.invoicePath IS NULL";

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return getPaginatedResults(SELECT_ALL_PAGINATED, page, size);
    }

    public long countAll() {
        return em.createQuery(COUNT_ALL, Long.class).getSingleResult();
    }

    public List<OrderPojo> searchByForm(OrderSearchForm form) {
        LocalDate start = LocalDate.parse(form.getStartDate());
        LocalDate end = LocalDate.parse(form.getEndDate());
        return buildDynamicQuery(start, end, form.getInvoiceGenerated(), form.getPage(), form.getSize());
    }

    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query, int page, int size) {
        return buildDynamicQuery(start, end, invoiceGenerated, page, size);
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        return buildDynamicCountQuery(start, end, invoiceGenerated);
    }

    public List<OrderPojo> getByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);
        return em.createQuery("SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end", OrderPojo.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    private List<OrderPojo> buildDynamicQuery(LocalDate start, LocalDate end, Boolean invoiceGenerated, int page, int size) {
        String jpql = buildQueryString(false, invoiceGenerated);
        TypedQuery<OrderPojo> query = em.createQuery(jpql, OrderPojo.class);
        setTimeParameters(query, start, end);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    private long buildDynamicCountQuery(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        String jpql = buildQueryString(true, invoiceGenerated);
        TypedQuery<Long> countQuery = em.createQuery(jpql, Long.class);
        setTimeParameters(countQuery, start, end);
        return countQuery.getSingleResult();
    }

    private String buildQueryString(boolean isCount, Boolean invoiceGenerated) {
        StringBuilder jpql = new StringBuilder(isCount ? COUNT_BASE : SELECT_BASE);
        jpql.append(BASE_WHERE_TIME_RANGE);
        
        if (invoiceGenerated != null) {
            jpql.append(invoiceGenerated ? INVOICE_GENERATED : INVOICE_NOT_GENERATED);
        }
        
        if (!isCount) {
            jpql.append(BASE_ORDER_BY);
        }
        
        return jpql.toString();
    }

    private void setTimeParameters(TypedQuery<?> query, LocalDate start, LocalDate end) {
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        query.setParameter("start", startZdt);
        query.setParameter("end", endZdt);
    }
}
