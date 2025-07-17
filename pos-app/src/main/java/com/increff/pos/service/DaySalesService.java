package com.increff.pos.service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.increff.pos.exception.ApiException;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class DaySalesService {

    @Autowired private DaySalesDao daySalesDao;

    @Transactional(rollbackFor = ApiException.class)
    public void insert(DaySalesPojo pojo) {
        daySalesDao.insert(pojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public DaySalesPojo getByDate(ZonedDateTime date) {
        return daySalesDao.getByDate(date.toLocalDate());
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<DaySalesPojo> getBetween(ZonedDateTime start, ZonedDateTime end) {
        return daySalesDao.getBetween(start.toLocalDate(), end.toLocalDate());
    }
}

