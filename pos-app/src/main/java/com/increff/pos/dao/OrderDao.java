package com.increff.pos.dao;

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

    public OrderDao() {
        super(OrderPojo.class);
    }

    private static final String SELECT_ALL_PAGINATED = "SELECT o FROM OrderPojo o ORDER BY o.time DESC";
    private static final String COUNT_ALL = "SELECT COUNT(o) FROM OrderPojo o";

    private static final String BASE_SELECT_FILTERED = "SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end";
    private static final String BASE_COUNT_FILTERED = "SELECT COUNT(o) FROM OrderPojo o WHERE o.time >= :start AND o.time < :end";
    private static final String INVOICE_NOT_NULL = " AND o.invoicePath IS NOT NULL";
    private static final String INVOICE_IS_NULL = " AND o.invoicePath IS NULL";
    private static final String QUERY_CLAUSE = " AND (STR(o.id) LIKE :q)";
    private static final String ORDER_BY_TIME_DESC = " ORDER BY o.time DESC";

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return em.createQuery(SELECT_ALL_PAGINATED, OrderPojo.class).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    public long countAll() {
        return em.createQuery(COUNT_ALL, Long.class).getSingleResult();
    }

    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query, int page, int size) {
        StringBuilder jpql = new StringBuilder(BASE_SELECT_FILTERED);
        if (invoiceGenerated != null) {
            jpql.append(invoiceGenerated ? INVOICE_NOT_NULL : INVOICE_IS_NULL);
        }
        if (query != null && !query.trim().isEmpty()) {
            jpql.append(QUERY_CLAUSE);
        }
        jpql.append(ORDER_BY_TIME_DESC);

        TypedQuery<OrderPojo> typedQuery = em.createQuery(jpql.toString(), OrderPojo.class);
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        typedQuery.setParameter("start", startZdt);
        typedQuery.setParameter("end", endZdt);


        if (query != null && !query.trim().isEmpty()) {
            typedQuery.setParameter("q", query.trim() + "%");
        }

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);
        return typedQuery.getResultList();
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        StringBuilder jpql = new StringBuilder(BASE_COUNT_FILTERED);
        if (invoiceGenerated != null) {
            jpql.append(invoiceGenerated ? INVOICE_NOT_NULL : INVOICE_IS_NULL);
        }
        if (query != null && !query.trim().isEmpty()) {
            jpql.append(QUERY_CLAUSE);
        }

        TypedQuery<Long> countQuery = em.createQuery(jpql.toString(), Long.class);
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        countQuery.setParameter("start", startZdt);
        countQuery.setParameter("end", endZdt);

        if (query != null && !query.trim().isEmpty()) {
            countQuery.setParameter("q", query.trim() + "%");
        }

        return countQuery.getSingleResult();
    }

    public List<OrderPojo> getByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);
        return em.createQuery("SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end", OrderPojo.class).setParameter("start", start).setParameter("end", end).getResultList();
    }
}
