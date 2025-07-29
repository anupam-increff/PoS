package com.increff.pos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.Validator;
import java.util.Properties;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.increff.pos")
@PropertySource("classpath:application.properties")
@EnableScheduling
public class SpringConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AppProperties appProperties;

    @Bean
    public DataSource dataSource() {
        org.apache.commons.dbcp2.BasicDataSource ds = new org.apache.commons.dbcp2.BasicDataSource();
        ds.setDriverClassName(appProperties.getJdbcDriver());
        ds.setUrl(appProperties.getJdbcUrl());
        ds.setUsername(appProperties.getJdbcUserName());
        ds.setPassword(appProperties.getJdbcPassword());
        ds.setMinIdle(appProperties.getMinConnection());
        ds.setMaxTotal(appProperties.getMaxConnection());
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.increff.pos.pojo");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties props = new Properties();
        props.put("hibernate.dialect", appProperties.getHibernateDialect());
        props.put("hibernate.show_sql", appProperties.getHibernateShowSql());
        props.put("hibernate.hbm2ddl.auto", appProperties.getHibernateHbm2ddlAuto());
        props.put("hibernate.jdbc.batch_size", appProperties.getHibernateBatchSize());
        props.put("hibernate.physical_naming_strategy", new SnakeCaseNamingStrategy("pos"));

        emf.setJpaProperties(props);
        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(appProperties.getMultipartMaxFileSize() * 1024 * 1024L);
        resolver.setMaxUploadSizePerFile(appProperties.getMultipartMaxRequestSize() * 1024 * 1024L);
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
