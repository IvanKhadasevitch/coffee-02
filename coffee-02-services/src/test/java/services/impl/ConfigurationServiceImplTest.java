package services.impl;

import dao.ConfigurationDao;
import entities.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import services.ConfigurationService;

import java.sql.SQLException;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurationServiceImplTest extends Assert {
    @Autowired
    private ConfigurationService configurationService;

    @Test
    public void getValue() throws SQLException {
        // null test
        assertNull(configurationService.getValue(null));

        //delete configuration if exist
        if (configurationService.get("n") != null) {
            configurationService.delete("n");
        }
        // check default value
        assertEquals(5.0, Double.valueOf(configurationService.getValue("n")),
                0.00001);

        // make & save configuration in DB
        Configuration configuration = new Configuration();
        configuration.setId("n");
        configuration.setValue("12");
        configuration = configurationService.add(configuration);
        assertEquals(12.0, Double.valueOf(configurationService.getValue("n")),
                0.00001);

        // update configuration in Db
        configuration.setValue("15");
        configurationService.update(configuration);
        assertEquals(15.0, Double.valueOf(configurationService.getValue("n")),
                0.00001);

        // delete saved configuration from DB
        int deletedRows = configurationService.delete(configuration.getId());
        assertEquals(1, deletedRows);
        Configuration configurationFromDB = configurationService.get(configuration.getId());
        assertNull(configurationFromDB);
    }
}
