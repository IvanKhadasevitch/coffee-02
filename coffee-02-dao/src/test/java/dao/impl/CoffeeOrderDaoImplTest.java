package dao.impl;

import dao.CoffeeOrderDao;
import db.ConnectionManager;
import entities.CoffeeOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.Date;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CoffeeOrderDaoImplTest extends Assert {

    @Autowired
    private CoffeeOrderDao coffeeOrderDao;

    @Test
    public void crud()  throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        CoffeeOrder saved;
        CoffeeOrder getIt;
        CoffeeOrder updated;

        CoffeeOrder newOneForSave = new CoffeeOrder();
        Date currentDate = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(currentDate.getTime());
        newOneForSave.setOrderDate(timestamp);
        newOneForSave.setCustomerName("Ivanov Ivan");
        newOneForSave.setDeliveryAddress("Sunny street, 12");
        newOneForSave.setCost(12.0);

        CoffeeOrder newOneForUpdate = new CoffeeOrder();
        newOneForUpdate.setOrderDate(timestamp);
        newOneForUpdate.setCustomerName("Pavlov Pavel");
        newOneForUpdate.setDeliveryAddress("Wide street, 10");
        newOneForUpdate.setCost(10.0);

        try {
            // check null save & update
            CoffeeOrder nullSave = coffeeOrderDao.save(null);
            assertNull(nullSave);
            coffeeOrderDao.update(nullSave);
            assertNull(nullSave);

            // check save and get
            saved = coffeeOrderDao.save(newOneForSave);
            getIt = coffeeOrderDao.get(saved.getId());

            assertNotNull(saved);
            assertNotNull(getIt);
            assertEquals(saved.getId(), getIt.getId());
            assertEquals(saved.getCustomerName(), getIt.getCustomerName());
            assertEquals(saved.getDeliveryAddress(), getIt.getDeliveryAddress());
            assertEquals(saved.getCost(), getIt.getCost(), 0.00001);

            // check update
            newOneForUpdate.setId(saved.getId());
            newOneForUpdate.setOrderDate(getIt.getOrderDate());
            coffeeOrderDao.update(newOneForUpdate);
            updated = coffeeOrderDao.get(newOneForUpdate.getId());

            assertNotEquals(saved, updated);
            assertNotNull(updated);
            assertEquals(newOneForUpdate,updated);
            assertEquals(saved.getId(),updated.getId());

            // check delete
            int delNumber = coffeeOrderDao.delete(getIt.getId());
            assertEquals(delNumber, 1);
            getIt = coffeeOrderDao.get(saved.getId());
            assertNull(getIt);

        } finally {
            con.rollback();
        }
    }
}
