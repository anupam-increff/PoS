# POS Application Test Suite

This document outlines the comprehensive test suite created for the POS application following the test organization structure specified in the cursor settings.

## Test Organization Structure

The tests are organized according to the following structure:

```
src/test/java/com/increff/pos/
├── setup/
│   └── TestData.java                    # Centralized test data factory
├── unit/                                # Unit tests (isolated, fast, mocked dependencies)
│   ├── dao/                            # DAO layer unit tests
│   │   ├── ClientDaoTest.java
│   │   └── ProductDaoTest.java
│   ├── flow/                           # Flow layer unit tests
│   │   └── ProductFlowTest.java
│   └── api/                            # API layer unit tests (Controllers)
│       └── ProductControllerTest.java
└── integration/                        # Integration tests (database, external dependencies)
    └── dto/                            # DTO integration tests organized by domain
        ├── product/
        │   └── ProductCreationIntegrationTests.java
        └── inventory/
            └── InventoryCreationIntegrationTests.java
```

## Test Categories

### 1. Unit Tests

#### DAO Layer Tests (`unit/dao/`)
- **ClientDaoTest.java**: Tests for ClientDao operations
  - `testInsert()`: Tests inserting a new client
  - `testGetClientByName()`: Tests retrieving client by name
  - `testGetClientByNameNotFound()`: Tests handling non-existent client
  - `testGetAllPaged()`: Tests paginated retrieval of all clients
  - `testCountAll()`: Tests counting all clients
  - `testSearchByQuery()`: Tests searching clients by query
  - `testCountByQuery()`: Tests counting clients by search query

- **ProductDaoTest.java**: Tests for ProductDao operations
  - `testGetByBarcode()`: Tests retrieving product by barcode
  - `testGetByBarcodeNotFound()`: Tests handling non-existent product
  - `testGetAllPaged()`: Tests paginated retrieval of all products
  - `testCountAll()`: Tests counting all products
  - `testGetByClientIdPaged()`: Tests retrieving products by client ID
  - `testCountByClientId()`: Tests counting products by client ID
  - `testSearchByBarcode()`: Tests searching products by barcode
  - `testCountByBarcodeSearch()`: Tests counting products by barcode search

#### Flow Layer Tests (`unit/flow/`)
- **ProductFlowTest.java**: Tests for ProductFlow business logic
  - `testAddProduct()`: Tests adding a new product
  - `testAddProductWithNonExistentClient()`: Tests validation for non-existent client
  - `testAddProductWithDuplicateBarcode()`: Tests duplicate barcode validation
  - `testGetAllProducts()`: Tests retrieving all products
  - `testGetProductsByClient()`: Tests retrieving products by client
  - `testSearchProductsByBarcode()`: Tests searching products by barcode
  - `testGetProductByBarcode()`: Tests retrieving product by barcode
  - `testGetProductByBarcodeNotFound()`: Tests handling non-existent product
  - `testUpdateProduct()`: Tests updating product
  - `testUpdateProductNotFound()`: Tests updating non-existent product
  - `testCountAllProducts()`: Tests counting all products
  - `testCountProductsByClient()`: Tests counting products by client
  - `testCountSearchByBarcode()`: Tests counting products by barcode search

### 2. Integration Tests

#### DTO Layer Tests (`integration/dto/`)

##### Product Domain (`integration/dto/product/`)
- **ProductCreationIntegrationTests.java**: Integration tests for Product DTO operations
  - `testAddProduct()`: Tests creating product through DTO with database verification
  - `testAddProductWithNonExistentClient()`: Tests validation for non-existent client
  - `testAddProductWithDuplicateBarcode()`: Tests duplicate barcode validation
  - `testGetAllProducts()`: Tests retrieving all products with pagination
  - `testGetProductsByClient()`: Tests retrieving products by client
  - `testSearchProductsByBarcode()`: Tests searching products by barcode
  - `testGetProductByBarcode()`: Tests retrieving product by barcode
  - `testGetProductByBarcodeNotFound()`: Tests handling non-existent product

##### Inventory Domain (`integration/dto/inventory/`)
- **InventoryCreationIntegrationTests.java**: Integration tests for Inventory DTO operations
  - `testAddInventory()`: Tests creating inventory through DTO with database verification
  - `testAddInventoryWithNonExistentProduct()`: Tests validation for non-existent product
  - `testAddInventoryWithNegativeQuantity()`: Tests validation for negative quantity
  - `testGetAllInventory()`: Tests retrieving all inventory with pagination
  - `testSearchInventoryByBarcode()`: Tests searching inventory by barcode
  - `testUpdateInventoryByBarcode()`: Tests updating inventory by barcode
  - `testUpdateInventoryByBarcodeNotFound()`: Tests updating non-existent inventory
  - `testUpdateInventoryBarcodeMismatch()`: Tests barcode mismatch validation
  - `testUploadInventory()`: Tests inventory upload functionality

## Test Data Factory

### TestData.java
Centralized factory for creating test entities:

- **Client**: `client(int id)`, `clientForm(String name)`
- **Product**: `product(int id, int clientId)`, `productForm(String barcode, String name, String clientName, Double mrp)`
- **Inventory**: `inventory(int id, int productId)`, `inventoryForm(String barcode, Integer quantity)`
- **Order**: `order(int id)`, `orderForm(List<OrderItemForm> items)`
- **OrderItem**: `orderItem(int id, int orderId, int productId)`, `orderItemForm(String barcode, Integer quantity, Double sellingPrice)`
- **DaySales**: `daySales(LocalDate date)`
- **SalesReport**: `salesReportFilterForm(ZonedDateTime startDate, ZonedDateTime endDate)`

## Test Patterns

### Unit Test Pattern
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config.xml"})
@Transactional
public class ComponentTest {
    
    @Autowired
    private Component component;
    
    @Test
    public void testMethod() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Integration Test Pattern
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config.xml"})
@Transactional
public class ComponentIntegrationTests {
    
    @Autowired
    private ComponentDto componentDto;
    
    @Autowired
    private ComponentDao componentDao;
    
    @Test
    public void testMethod() {
        // Arrange - Create data using DAO
        // Act - Execute through DTO method
        // Assert - Verify DTO results and database state using DAO
    }
}
```

## Key Testing Principles

1. **Isolation**: Unit tests are isolated with mocked dependencies
2. **Integration**: Integration tests verify end-to-end functionality
3. **Database Verification**: Integration tests verify both DTO results and database state
4. **Error Handling**: Tests cover both success and error scenarios
5. **Validation**: Tests verify business rule validation
6. **Pagination**: Tests verify pagination functionality
7. **Search**: Tests verify search functionality
8. **CRUD Operations**: Tests cover Create, Read, Update operations

## Test Coverage

The test suite covers:

- **DAO Layer**: All CRUD operations, search, and pagination
- **Flow Layer**: Business logic, validation, and error handling
- **DTO Layer**: Integration between layers, data transformation
- **API Layer**: HTTP endpoints, request/response handling
- **Validation**: Form validation, business rule validation
- **Error Scenarios**: Non-existent entities, validation errors, duplicates
- **Pagination**: Page-based retrieval with proper counting
- **Search**: Barcode search, client-based filtering

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test category
mvn test -Dtest=*DaoTest
mvn test -Dtest=*FlowTest
mvn test -Dtest=*IntegrationTests

# Run specific test class
mvn test -Dtest=ProductDaoTest
mvn test -Dtest=ProductCreationIntegrationTests
```

## Test Configuration

Tests use:
- **Spring Test**: For dependency injection and transaction management
- **JUnit 4**: For test framework
- **@Transactional**: For automatic rollback after each test
- **TestData**: For consistent test data creation
- **Assertions**: JUnit assertions for verification

## Future Enhancements

1. **Mockito Integration**: Add Mockito for better unit test mocking
2. **API Tests**: Add MockMvc tests for controller endpoints
3. **Performance Tests**: Add performance benchmarks
4. **Security Tests**: Add authentication and authorization tests
5. **TSV Upload Tests**: Add comprehensive TSV upload/download tests
6. **Order Domain Tests**: Add tests for Order and OrderItem domains
7. **Sales Report Tests**: Add tests for reporting functionality 