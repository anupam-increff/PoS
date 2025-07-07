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
    private static final String ORDER_BY_TIME_DESC = " ORDER BY o.time DESC";

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return em.createQuery(SELECT_ALL_PAGINATED, OrderPojo.class).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    public long countAll() {
        return em.createQuery(COUNT_ALL, Long.class).getSingleResult();
    }

    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, int page, int size) {
        String jpql = BASE_SELECT_FILTERED;
        if (invoiceGenerated != null) {
            jpql += invoiceGenerated ? INVOICE_NOT_NULL : INVOICE_IS_NULL;
        }
        jpql += ORDER_BY_TIME_DESC;

        TypedQuery<OrderPojo> query = em.createQuery(jpql, OrderPojo.class);
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        query.setParameter("start", startZdt);
        query.setParameter("end", endZdt);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        String jpql = BASE_COUNT_FILTERED;
        if (invoiceGenerated != null) {
            jpql += invoiceGenerated ? INVOICE_NOT_NULL : INVOICE_IS_NULL;
        }

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        query.setParameter("start", startZdt);
        query.setParameter("end", endZdt);
        return query.getSingleResult();
    }

    public List<OrderPojo> getByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);
        return em.createQuery("SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end", OrderPojo.class).setParameter("start", start).setParameter("end", end).getResultList();
    }

}
