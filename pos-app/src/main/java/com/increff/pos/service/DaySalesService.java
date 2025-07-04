package com.increff.pos.service;

import com.increff.pos.dao.DaySalesDao;
import com.increff.pos.pojo.DaySalesPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DaySalesService {

    @Autowired private DaySalesDao daySalesDao;

    @Transactional
    public void insert(DaySalesPojo pojo) {
        daySalesDao.insert(pojo);
    }

    @Transactional
    public DaySalesPojo getByDate(LocalDate date) {
        return daySalesDao.getByDate(date);
    }

    @Transactional
    public List<DaySalesPojo> getBetween(LocalDate start, LocalDate end) {
        return daySalesDao.getBetween(start, end);
    }
}

