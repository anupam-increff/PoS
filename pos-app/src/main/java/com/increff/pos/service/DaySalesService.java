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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DaySalesService {

    @Autowired private DaySalesDao daySalesDao;

    @Transactional(rollbackFor = ApiException.class)
    public void insert(DaySalesPojo pojo) {
        daySalesDao.insert(pojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public DaySalesPojo getByDate(LocalDate date) {
        return daySalesDao.getByDate(date);
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<DaySalesPojo> getBetween(LocalDate start, LocalDate end) {
        return daySalesDao.getBetween(start, end);
    }
}

