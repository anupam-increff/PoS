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

    public OrderDao() {
        super(OrderPojo.class);
    }

    private static final String SELECT_ALL_PAGINATED = "SELECT o FROM OrderPojo o ORDER BY o.time DESC";
    private static final String COUNT_ALL = "SELECT COUNT(o) FROM OrderPojo o";

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return getPaginatedResults(SELECT_ALL_PAGINATED, page, size);
    }

    public long countAll() {
        return em.createQuery(COUNT_ALL, Long.class).getSingleResult();
    }


    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query, int page, int size) {
        return buildDynamicQuery(start, end, invoiceGenerated, page, size);
    }

    private List<OrderPojo> buildDynamicQuery(LocalDate start, LocalDate end, Boolean invoiceGenerated, int page, int size) {
        StringBuilder jpql = new StringBuilder("SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end");
        
        if (invoiceGenerated != null) {
            jpql.append(invoiceGenerated ? " AND o.invoicePath IS NOT NULL" : " AND o.invoicePath IS NULL");
        }
        
        jpql.append(" ORDER BY o.time DESC");

        TypedQuery<OrderPojo> query = em.createQuery(jpql.toString(), OrderPojo.class);
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        
        query.setParameter("start", startZdt);
        query.setParameter("end", endZdt);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        
        return query.getResultList();
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(o) FROM OrderPojo o WHERE o.time >= :start AND o.time < :end");
        
        if (invoiceGenerated != null) {
            jpql.append(invoiceGenerated ? " AND o.invoicePath IS NOT NULL" : " AND o.invoicePath IS NULL");
        }

        TypedQuery<Long> countQuery = em.createQuery(jpql.toString(), Long.class);
        ZonedDateTime startZdt = start.atStartOfDay().atZone(ZoneOffset.UTC);
        ZonedDateTime endZdt = end.plusDays(1).atStartOfDay().atZone(ZoneOffset.UTC);
        
        countQuery.setParameter("start", startZdt);
        countQuery.setParameter("end", endZdt);

        return countQuery.getSingleResult();
    }

    public List<OrderPojo> getByDate(LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime end = start.plusDays(1);
        return em.createQuery("SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end", OrderPojo.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
