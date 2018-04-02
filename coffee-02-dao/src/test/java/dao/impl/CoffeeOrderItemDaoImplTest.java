package dao.impl;

import dao.CoffeeOrderDao;
import dao.CoffeeOrderItemDao;
import dao.CoffeeTypeDao;
import db.ConnectionManager;
import entities.CoffeeOrder;
import entities.CoffeeOrderItem;
import entities.CoffeeType;
import entities.enums.DisabledFlag;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CoffeeOrderItemDaoImplTest extends Assert {
    @Autowired
    private CoffeeOrderItemDao coffeeOrderItemDao;
    @Autowired
    private CoffeeOrderDao coffeeOrderDao;
    @Autowired
    private CoffeeTypeDao coffeeTypeDao;

    @Test
    public void crud()  throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        CoffeeOrderItem saved;
        CoffeeOrderItem getIt;
        CoffeeOrderItem updated;

        CoffeeOrderItem newOneForSave = new CoffeeOrderItem();
        newOneForSave.setQuantity(3);

        CoffeeType coffeeTypeForSave = new CoffeeType();
        coffeeTypeForSave.setTypeName("Very fragrant coffee");
        coffeeTypeForSave.setPrice(1.0);
        coffeeTypeForSave.setDisabled(DisabledFlag.Y);

        CoffeeOrder coffeeOrderForSave = new CoffeeOrder();
        Date currentDate = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(currentDate.getTime());
        coffeeOrderForSave.setOrderDate(timestamp);
        coffeeOrderForSave.setCustomerName("Ivanov Ivan");
        coffeeOrderForSave.setDeliveryAddress("Sunny street, 12");
        coffeeOrderForSave.setCost(12.0);

        try {
            // check null save & update
            CoffeeOrderItem nullSave = coffeeOrderItemDao.save(null);
            assertNull(nullSave);
            coffeeOrderItemDao.update(nullSave);
            assertNull(nullSave);

            // save CoffeeType & CoffeeOrder
            coffeeTypeForSave = coffeeTypeDao.save(coffeeTypeForSave);
            coffeeOrderForSave = coffeeOrderDao.save(coffeeOrderForSave);

            // check save and get
            newOneForSave.setCoffeeTypeId(coffeeTypeForSave.getId());
            newOneForSave.setOrderId(coffeeOrderForSave.getId());
            saved = coffeeOrderItemDao.save(newOneForSave);
            getIt = coffeeOrderItemDao.get(saved.getId());

            assertNotNull(saved);
            assertNotNull(getIt);
            assertEquals(saved, getIt);

            // check update
            CoffeeOrderItem newOneForUpdate = coffeeOrderItemDao.get(saved.getId());
            newOneForUpdate.setQuantity(5);
            coffeeOrderItemDao.update(newOneForUpdate);
            updated = coffeeOrderItemDao.get(newOneForUpdate.getId());

            assertNotEquals(saved, updated);
            assertNotNull(updated);
            assertEquals(newOneForUpdate, updated);
            assertEquals(saved.getId(), updated.getId());

            // check delete
            int delNumber = coffeeOrderItemDao.delete(getIt.getId());
            assertEquals(delNumber, 1);
            getIt = coffeeOrderItemDao.get(saved.getId());
            assertNull(getIt);

            delNumber = coffeeTypeDao.delete(coffeeTypeForSave.getId());
            assertEquals(delNumber, 1);
            CoffeeType getCoffeeType = coffeeTypeDao.get(coffeeTypeForSave.getId());
            assertNull(getCoffeeType);

            delNumber = coffeeOrderDao.delete(coffeeOrderForSave.getId());
            assertEquals(delNumber, 1);
            CoffeeOrder getCoffeeOrder = coffeeOrderDao.get(coffeeTypeForSave.getId());
            assertNull(getCoffeeOrder);

        } finally {
            con.rollback();
        }
    }

    @Test
    public void getAllForOrderId() throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        CoffeeOrderItem newOneForSave = new CoffeeOrderItem();
        newOneForSave.setQuantity(3);

        CoffeeType coffeeTypeForSave = new CoffeeType();
        coffeeTypeForSave.setTypeName("Very fragrant coffee");
        coffeeTypeForSave.setPrice(1.0);
        coffeeTypeForSave.setDisabled(DisabledFlag.Y);

        CoffeeOrder coffeeOrderForSave = new CoffeeOrder();
        Date currentDate = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(currentDate.getTime());
        coffeeOrderForSave.setOrderDate(timestamp);
        coffeeOrderForSave.setCustomerName("Ivanov Ivan");
        coffeeOrderForSave.setDeliveryAddress("Sunny street, 12");
        coffeeOrderForSave.setCost(12.0);

        try {
            // save CoffeeType & CoffeeOrder
            coffeeTypeForSave = coffeeTypeDao.save(coffeeTypeForSave);
            coffeeOrderForSave = coffeeOrderDao.save(coffeeOrderForSave);

            // check getAllForOrderId
            List<CoffeeOrderItem> list1 = coffeeOrderItemDao.getAllForOrderId(coffeeOrderForSave.getId());

            newOneForSave.setCoffeeTypeId(coffeeTypeForSave.getId());
            newOneForSave.setOrderId(coffeeOrderForSave.getId());
            newOneForSave = coffeeOrderItemDao.save(newOneForSave);
            List<CoffeeOrderItem> list2 = coffeeOrderItemDao.getAllForOrderId(coffeeOrderForSave.getId());

            assertEquals(list2.size() - list1.size() , 1);

            // delete saved entities
            int delNumber = coffeeOrderItemDao.delete(newOneForSave.getId());
            assertEquals(delNumber, 1);
            CoffeeOrderItem getCoffeeOrderItem = coffeeOrderItemDao.get(newOneForSave.getId());
            assertNull(getCoffeeOrderItem);

            delNumber = coffeeTypeDao.delete(coffeeTypeForSave.getId());
            assertEquals(delNumber, 1);
            CoffeeType getCoffeeType = coffeeTypeDao.get(coffeeTypeForSave.getId());
            assertNull(getCoffeeType);

            delNumber = coffeeOrderDao.delete(coffeeOrderForSave.getId());
            assertEquals(delNumber, 1);
            CoffeeOrder getCoffeeOrder = coffeeOrderDao.get(coffeeTypeForSave.getId());
            assertNull(getCoffeeOrder);
        } finally {
            con.rollback();
        }
    }
}
