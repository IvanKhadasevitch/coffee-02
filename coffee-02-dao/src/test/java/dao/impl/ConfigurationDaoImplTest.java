package dao.impl;

import dao.ConfigurationDao;
import db.ConnectionManager;
import entities.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;

@ContextConfiguration("/testContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurationDaoImplTest extends Assert {
    @Autowired
    private ConfigurationDao configurationDao;

    @Test
    public void crud()  throws SQLException {
        Connection con = ConnectionManager.getConnection();
        con.setAutoCommit(false);

        Configuration saved;
        Configuration getIt;
        Configuration updated;

        Configuration newOneForSave = new Configuration();
        newOneForSave.setId("x");
        newOneForSave.setValue("3");

        Configuration newOneForUpdate = new Configuration();
        newOneForUpdate.setValue("1");

        try {
            // check null save
            Configuration nullSave = configurationDao.save(null);
            assertNull(nullSave);
            configurationDao.update(nullSave);
            assertNull(nullSave);

            nullSave = new Configuration();
            assertNotNull(nullSave);
            assertNull(nullSave.getId());
            nullSave = configurationDao.save(null);
            assertNull(nullSave);


            // check save and get
            saved = configurationDao.save(newOneForSave);
            getIt = configurationDao.get(saved.getId());

            assertNotNull(saved);
            assertNotNull(getIt);
            assertEquals(saved, getIt);

            // check update
            newOneForUpdate.setId(saved.getId());
            configurationDao.update(newOneForUpdate);
            updated = configurationDao.get(newOneForUpdate.getId());

            assertNotEquals(saved, updated);
            assertNotNull(updated);
            assertEquals(newOneForUpdate,updated);
            assertEquals(saved.getId(),updated.getId());

            // check delete
            int delNumber = configurationDao.delete(getIt.getId());
            assertEquals(delNumber, 1);
            getIt = configurationDao.get(saved.getId());
            assertNull(getIt);

        } finally {
            con.rollback();
        }
    }
}
