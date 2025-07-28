package com.increff.pos.service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.exception.ApiException;
import com.increff.pos.pojo.DaySalesPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = ApiException.class)
public class DaySalesService {

    @Autowired private DaySalesDao daySalesDao;

    @Transactional(rollbackFor = ApiException.class)
    public void insert(DaySalesPojo pojo) {
        daySalesDao.insert(pojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public DaySalesPojo getByDate(ZonedDateTime date) {
        return daySalesDao.getReportForDate(date);
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<DaySalesPojo> getBetween(ZonedDateTime start, ZonedDateTime end) {
        return daySalesDao.getReportBetweenDates(start, end);
    }
}

