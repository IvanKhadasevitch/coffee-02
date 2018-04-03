package dao.impl;

import dao.CoffeeTypeDao;
import db.ConnectionManager;
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
import java.util.List;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CoffeeTypeDaoImplTest extends Assert {
    @Autowired
    private CoffeeTypeDao coffeeTypeDao;

    @Test
    public void crud()  throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);
        CoffeeType saved;
        CoffeeType getIt;
        CoffeeType updated;

        CoffeeType newOneForSave = new CoffeeType();
        newOneForSave.setTypeName("Very fragrant coffee");
        newOneForSave.setPrice(1.0);
        newOneForSave.setDisabled(DisabledFlag.Y);

        CoffeeType newOneForUpdate = new CoffeeType();
        newOneForUpdate.setTypeName("Black coffee with cream");
        newOneForUpdate.setPrice(3.0);
        newOneForUpdate.setDisabled(DisabledFlag.N);

        try {
            // check null save & update
            CoffeeType nullSave = coffeeTypeDao.save(null);
            assertNull(nullSave);
            coffeeTypeDao.update(nullSave);
            assertNull(nullSave);

            // check save and get
            saved = coffeeTypeDao.save(newOneForSave);
            getIt = coffeeTypeDao.get(saved.getId());

            assertNotNull(saved);
            assertNotNull(getIt);
            assertEquals(saved, getIt);

            // check update
            newOneForUpdate.setId(saved.getId());
            coffeeTypeDao.update(newOneForUpdate);
            updated = coffeeTypeDao.get(newOneForUpdate.getId());

            assertNotEquals(saved, updated);
            assertNotNull(updated);
            assertEquals(newOneForUpdate,updated);
            assertEquals(saved.getId(),updated.getId());

            // check delete
            int delNumber = coffeeTypeDao.delete(getIt.getId());
            assertEquals(delNumber, 1);
            getIt = coffeeTypeDao.get(saved.getId());
            assertNull(getIt);

        } finally {
            con.rollback();
        }
    }

    @Test
    public void getAll() throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        CoffeeType newOneForSave = new CoffeeType();
        newOneForSave.setTypeName("Very fragrant coffee");
        newOneForSave.setPrice(1.0);
        newOneForSave.setDisabled(DisabledFlag.Y);
        try {
            List<CoffeeType> list1 = coffeeTypeDao.getAll();

            coffeeTypeDao.save(newOneForSave);
            List<CoffeeType> list2 = coffeeTypeDao.getAll();

            assertEquals(list2.size() - list1.size() , 1);

            for (CoffeeType element : list2) {
                coffeeTypeDao.delete(element.getId());
            }
            List<CoffeeType> listAfterDeleteAll = coffeeTypeDao.getAll();
            assertEquals(listAfterDeleteAll.size(),0);
        } finally {
            con.rollback();
        }
    }

    @Test
    public void getAllForDisabledFlag() throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        CoffeeType newOneForSave = new CoffeeType();
        newOneForSave.setTypeName("Very fragrant coffee");
        newOneForSave.setPrice(1.0);
        newOneForSave.setDisabled(DisabledFlag.Y);
        try {
            List<CoffeeType> list1 = coffeeTypeDao.getAllForDisabledFlag(DisabledFlag.Y);

            coffeeTypeDao.save(newOneForSave);
            newOneForSave.setDisabled(DisabledFlag.N);
            newOneForSave.setTypeName("Black coffee with cream");
            coffeeTypeDao.save(newOneForSave);
            List<CoffeeType> list2 = coffeeTypeDao.getAllForDisabledFlag(DisabledFlag.Y);

            assertEquals(list2.size() - list1.size() , 1);

            for (CoffeeType element : coffeeTypeDao.getAll()) {
                coffeeTypeDao.delete(element.getId());
            }
            List<CoffeeType> listAfterDeleteAll = coffeeTypeDao.getAll();
            assertEquals(listAfterDeleteAll.size(),0);
        } finally {
            con.rollback();
        }
    }
}
