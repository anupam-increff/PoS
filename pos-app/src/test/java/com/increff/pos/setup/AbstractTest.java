package com.increff.pos.setup;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base test class for all integration and database tests.
 * Provides common Spring configuration, transaction management, and test utilities.
 * Always uses TestContainers for database testing.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestContainerConfig.class)
@ActiveProfiles("testcontainers")
@Transactional
@Rollback
public abstract class AbstractTest {
    
    /**
     * Common test setup method that can be overridden by subclasses.
     * Called before each test method.
     */
    protected void setUp() {
        // Default implementation - can be overridden
    }

} 