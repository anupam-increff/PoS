package com.increff.pos.scheduler;

import com.increff.pos.flow.DaySalesFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class DaySalesScheduler {

    @Autowired
    private DaySalesFlow daySalesFlow;

    @Scheduled(cron = "00 59 23 * * *", zone = "UTC")
    public void runDailySalesCalculation() {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        LocalDate today = nowUtc.toLocalDate();
        daySalesFlow.calculateDailySales(today);
    }
}
