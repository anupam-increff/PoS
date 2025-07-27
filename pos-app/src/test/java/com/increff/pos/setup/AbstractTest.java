package com.increff.pos.setup;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

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
    
    /**
     * Common test cleanup method that can be overridden by subclasses.
     * Called after each test method.
     */
    protected void tearDown() {
        // Default implementation - can be overridden
    }
    
    /**
     * Utility method to assert that a value is not null with a descriptive message.
     */
    protected void assertNotNullWithMessage(String message, Object value) {
        if (value == null) {
            throw new AssertionError(message + " should not be null");
        }
    }
    
    /**
     * Utility method to assert that two values are equal with a descriptive message.
     */
    protected void assertEqualsWithMessage(String message, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", but was: " + actual);
        }
    }
} 