package com.increff.pos.unit.dao;

import com.increff.pos.dao.ClientDao;
import com.increff.pos.pojo.ClientPojo;
import com.increff.pos.setup.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {com.increff.pos.setup.DaoTestConfig.class})
@Transactional
public class ClientDaoTest {

    @Autowired
    private ClientDao clientDao;

    private static int testCounter = 0;

    @Before
    public void setUp() {
        // Clean up any existing data
        // This will be handled by @Transactional rollback
    }

    private String getUniqueClientName(String baseName) {
        return baseName + "_" + System.currentTimeMillis() + "_" + (++testCounter);
    }

    @Test
    public void testInsert() {
        // Arrange
        String uniqueName = getUniqueClientName("TestClientInsert");
        ClientPojo client = TestData.clientWithoutId(uniqueName);

        // Act
        clientDao.insert(client);

        // Assert
        ClientPojo result = clientDao.getClientByName(uniqueName);
        assertNotNull(result);
        assertEquals(uniqueName, result.getName());
    }

    @Test
    public void testGetClientByName() {
        // Arrange
        String uniqueName = getUniqueClientName("TestClientGetByName");
        ClientPojo client = TestData.clientWithoutId(uniqueName);
        clientDao.insert(client);

        // Act
        ClientPojo result = clientDao.getClientByName(uniqueName);

        // Assert
        assertNotNull(result);
        assertEquals(uniqueName, result.getName());
    }

    @Test
    public void testGetClientByNameNotFound() {
        // Act
        ClientPojo result = clientDao.getClientByName("NonExistentClient");

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetAllPaged() {
        // Arrange
        String uniqueName1 = getUniqueClientName("TestClientPaged1");
        String uniqueName2 = getUniqueClientName("TestClientPaged2");
        ClientPojo client1 = TestData.clientWithoutId(uniqueName1);
        ClientPojo client2 = TestData.clientWithoutId(uniqueName2);
        clientDao.insert(client1);
        clientDao.insert(client2);

        // Act
        List<ClientPojo> result = clientDao.getAllPaged(0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    public void testCountAll() {
        // Arrange
        String uniqueName1 = getUniqueClientName("TestClientCount1");
        String uniqueName2 = getUniqueClientName("TestClientCount2");
        ClientPojo client1 = TestData.clientWithoutId(uniqueName1);
        ClientPojo client2 = TestData.clientWithoutId(uniqueName2);
        clientDao.insert(client1);
        clientDao.insert(client2);

        // Act
        long result = clientDao.countAll();

        // Assert
        assertTrue(result >= 2);
    }

    @Test
    public void testSearchByQuery() {
        // Arrange
        String uniqueName1 = getUniqueClientName("TestClientSearch1");
        String uniqueName2 = getUniqueClientName("TestClientSearch2");
        ClientPojo client1 = TestData.clientWithoutId(uniqueName1);
        ClientPojo client2 = TestData.clientWithoutId(uniqueName2);
        clientDao.insert(client1);
        clientDao.insert(client2);

        // Act
        List<ClientPojo> result = clientDao.searchByQuery("TestClientSearch", 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        for (ClientPojo client : result) {
            assertTrue(client.getName().toLowerCase().contains("testclientsearch"));
        }
    }

    @Test
    public void testCountByQuery() {
        // Arrange
        String uniqueName1 = getUniqueClientName("TestClientCountQuery1");
        String uniqueName2 = getUniqueClientName("TestClientCountQuery2");
        ClientPojo client1 = TestData.clientWithoutId(uniqueName1);
        ClientPojo client2 = TestData.clientWithoutId(uniqueName2);
        clientDao.insert(client1);
        clientDao.insert(client2);

        // Act
        long result = clientDao.countByQuery("TestClientCountQuery");

        // Assert
        assertTrue(result >= 2);
    }
} 