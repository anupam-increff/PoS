package com.increff.pos.scheduler;

import com.increff.pos.flow.DaySalesFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class DaySalesScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DaySalesScheduler.class);
    
    @Autowired
    private DaySalesFlow daySalesFlow;

    // Run at 23:30 every day (safer than 23:59)
    @Scheduled(cron = "0 30 23 * * *", zone = "Asia/Kolkata")
    public void runDailySalesCalculation() {
        try {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            LocalDate today = now.toLocalDate();
            
            logger.info("Starting daily sales calculation for date: {}", today);
            daySalesFlow.calculateDailySales(today);
            logger.info("Completed daily sales calculation for date: {}", today);
        } catch (Exception e) {
            logger.error("Error in daily sales calculation: ", e);
        }
    }
    
    // Manual method to generate reports for past dates
    public void generatePastReports(int daysBack) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(daysBack);
            
            logger.info("Generating sales reports from {} to {}", startDate, endDate);
            
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (daySalesFlow.getBetween(date, date).isEmpty()) {
                    logger.info("Generating report for missing date: {}", date);
                    daySalesFlow.calculateDailySales(date);
                }
            }
            
            logger.info("Completed generating past sales reports");
        } catch (Exception e) {
            logger.error("Error generating past reports: ", e);
        }
    }
    
    // Method to generate reports for a specific date range
    public void generateReportsForDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Generating sales reports for date range: {} to {}", startDate, endDate);
            
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                daySalesFlow.calculateDailySales(date);
            }
            
            logger.info("Completed generating reports for date range");
        } catch (Exception e) {
            logger.error("Error generating reports for date range: ", e);
        }
    }
}
