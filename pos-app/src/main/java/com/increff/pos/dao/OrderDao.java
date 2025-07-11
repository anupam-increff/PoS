package com.increff.pos.dao;

import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(rollbackFor = ApiException.class)
public class OrderDao extends AbstractDao<OrderPojo> {

    private static final String SELECT_ALL_PAGINATED = "SELECT o FROM OrderPojo o ORDER BY o.time DESC";
    private static final String SELECT_BY_DATE_RANGE = "SELECT o FROM OrderPojo o WHERE o.time >= :start AND o.time < :end";

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> getAllPaginated(int page, int size) {
        return getPaginatedResults(SELECT_ALL_PAGINATED, page, size, null);
    }

    public List<OrderPojo> search(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query, int page, int size) {
        CriteriaQuery<OrderPojo> cq = buildSearchCriteria(start, end, invoiceGenerated);
        return getPaginatedCriteriaResult(cq, page, size);
    }

    public long countMatching(LocalDate start, LocalDate end, Boolean invoiceGenerated, String query) {
        CriteriaQuery<Long> cq = buildCountCriteria(start, end, invoiceGenerated);
        return em.createQuery(cq).getSingleResult();
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

    private CriteriaQuery<OrderPojo> buildSearchCriteria(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> cq = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> root = cq.from(OrderPojo.class);

        Predicate filters = buildPredicates(cb, root, start, end, invoiceGenerated);
        cq.select(root).where(filters).orderBy(cb.desc(root.get("time")));
        return cq;
    }

    private CriteriaQuery<Long> buildCountCriteria(LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<OrderPojo> root = cq.from(OrderPojo.class);

        Predicate filters = buildPredicates(cb, root, start, end, invoiceGenerated);
        cq.select(cb.count(root)).where(filters);
        return cq;
    }

    private Predicate buildPredicates(CriteriaBuilder cb, Root<OrderPojo> root,
                                      LocalDate start, LocalDate end, Boolean invoiceGenerated) {
        List<Predicate> predicates = new ArrayList<>();

        if (start != null && end != null) {
            ZonedDateTime startZdt = start.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime endZdt = end.plusDays(1).atStartOfDay(ZoneOffset.UTC);
            predicates.add(cb.between(root.get("time"), startZdt, endZdt));
        }

        if (invoiceGenerated != null) {
            predicates.add(invoiceGenerated
                    ? cb.isNotNull(root.get("invoicePath"))
                    : cb.isNull(root.get("invoicePath")));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private List<OrderPojo> getPaginatedCriteriaResult(CriteriaQuery<OrderPojo> cq, int page, int size) {
        TypedQuery<OrderPojo> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
