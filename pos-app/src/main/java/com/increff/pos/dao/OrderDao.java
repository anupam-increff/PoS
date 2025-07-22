package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {

    private static final String SELECT_ALL_ORDERS_BY_DATE = "SELECT o FROM OrderPojo o ORDER BY o.createdAt DESC";
    private static final String SELECT_ORDERS_IN_DATE_RANGE = "SELECT o FROM OrderPojo o WHERE o.createdAt BETWEEN :startDate AND :endDate";
    private static final String COUNT_ORDERS_IN_DATE_RANGE = "SELECT COUNT(o) FROM OrderPojo o WHERE o.createdAt BETWEEN :startDate AND :endDate";
    private static final String SELECT_ORDERS_FOR_SPECIFIC_DATE = "SELECT o FROM OrderPojo o WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay ORDER BY o.createdAt DESC";
    private static final String ORDER_ID_SEARCH_FILTER = " AND CAST(o.id AS string) LIKE :searchQuery";
    private static final String ORDER_BY_CREATION_DATE_DESC = " ORDER BY o.createdAt DESC";

    public OrderDao() {
        super(OrderPojo.class);
    }

    public List<OrderPojo> searchOrders(ZonedDateTime startDate, ZonedDateTime endDate, String searchQuery, int pageNumber, int pageSize) {
        String searchQueryJpql = buildSearchQuery(searchQuery);
        Map<String, Object> queryParameters = buildQueryParameters(startDate, endDate, searchQuery);
        return getPaginatedResults(searchQueryJpql, pageNumber, pageSize, queryParameters);
    }

    public long countMatchingOrders(ZonedDateTime startDate, ZonedDateTime endDate, String searchQuery) {
        String countQueryJpql = buildCountQuery(searchQuery);
        Map<String, Object> queryParameters = buildQueryParameters(startDate, endDate, searchQuery);
        return getCount(countQueryJpql, queryParameters);
    }

    public List<OrderPojo> getOrdersForSpecificDate(ZonedDateTime targetDate) {
        ZonedDateTime dayStartTime = targetDate.toLocalDate().atStartOfDay(targetDate.getZone());
        ZonedDateTime dayEndTime = dayStartTime.plusDays(1);
        
        return em.createQuery(SELECT_ORDERS_FOR_SPECIFIC_DATE, OrderPojo.class)
                .setParameter("startOfDay", dayStartTime)
                .setParameter("endOfDay", dayEndTime)
                .getResultList();
    }

    public List<OrderPojo> getAllOrdersByDate(int pageNumber, int pageSize) {
        return getPaginatedResults(SELECT_ALL_ORDERS_BY_DATE, pageNumber, pageSize, null);
    }

    private String buildSearchQuery(String searchQuery) {
        StringBuilder queryBuilder = new StringBuilder(SELECT_ORDERS_IN_DATE_RANGE);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            queryBuilder.append(ORDER_ID_SEARCH_FILTER);
        }
        queryBuilder.append(ORDER_BY_CREATION_DATE_DESC);
        return queryBuilder.toString();
    }

    private String buildCountQuery(String searchQuery) {
        StringBuilder queryBuilder = new StringBuilder(COUNT_ORDERS_IN_DATE_RANGE);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            queryBuilder.append(ORDER_ID_SEARCH_FILTER);
        }
        return queryBuilder.toString();
    }

    private Map<String, Object> buildQueryParameters(ZonedDateTime startDate, ZonedDateTime endDate, String searchQuery) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            parameters.put("searchQuery", toLikePattern(searchQuery.toLowerCase()));
        }
        
        return parameters;
    }
}
