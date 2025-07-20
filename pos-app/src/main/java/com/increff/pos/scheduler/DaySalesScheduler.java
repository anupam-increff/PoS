package com.increff.pos.scheduler;

import com.increff.pos.flow.DaySalesFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class DaySalesScheduler {

    @Autowired
    private DaySalesFlow daySalesFlow;

    // Run daily at 23:59 UTC
    @Scheduled(cron = "0 59 23 * * *", zone = "UTC")
    public void runDailySalesCalculation() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        daySalesFlow.calculateDailySales(now);
    }
}
