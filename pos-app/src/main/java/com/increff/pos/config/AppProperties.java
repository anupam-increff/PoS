package com.increff.pos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class AppProperties {

    @Value("${jdbc.driver}")
    private String jdbcDriver;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.user}")
    private String jdbcUserName;

    @Value("${jdbc.pass}")
    private String jdbcPassword;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.show_sql:false}")
    private String hibernateShowSql;

    @Value("${hibernate.jdbc.batch_size:20}")
    private String hibernateBatchSize;

    @Value("${hibernate.jdbc.min_connection:10}")
    private int minConnection;

    @Value("${hibernate.jdbc.max_connection:30}")
    private int maxConnection;

    @Value("${hibernate.hbm2ddl.auto:none}")
    private String hibernateHbm2ddlAuto;

    @Value("${multipart.max-file-size:5}")
    private int multipartMaxFileSize;

    @Value("${multipart.max-request-size:5}")
    private int multipartMaxRequestSize;

    @Value("${pagination.default-page:0}")
    private int paginationDefaultPage;

    @Value("${pagination.default-page-size:5}")
    private int paginationDefaultPageSize;

    @Value("${pagination.max-page-size:100}")
    private int paginationMaxPageSize;

    @Value("${scheduler.day-sales-cron:0 59 23 * * *}")
    private String schedulerDaySalesCron;

    @Value("${auth.supervisor.emails:admin@increff.com}")
    private String authSupervisorEmails;

    @Value("${auth.default.password:pos123}")
    private String authDefaultPassword;
} 