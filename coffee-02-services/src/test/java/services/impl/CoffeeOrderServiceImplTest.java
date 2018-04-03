package services.impl;

import entities.CoffeeOrder;
import entities.CoffeeOrderItem;
import entities.CoffeeType;
import entities.Configuration;
import entities.enums.DisabledFlag;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import services.*;
import vo.CoffeeOrderAndCost;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CoffeeOrderServiceImplTest extends Assert {
    @Autowired
    private CoffeeOrderService coffeeOrderService;
    @Autowired
    private CoffeeTypeService coffeeTypeService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private CoffeeOrderItemService coffeeOrderItemService;

    @Test (expected = ServiceException.class)
    public void costCalculateSQLException() {
        List<CoffeeOrderItem> coffeeOrderItemList = new LinkedList<>();
        CoffeeOrderItem coffeeOrderItem = new CoffeeOrderItem();
        coffeeOrderItemList.add(coffeeOrderItem);
        coffeeOrderService.makeOrder("TEST Petrov", "TEST Street", coffeeOrderItemList);

    }

    @Test
    public void makeOrder() throws SQLException {
        // make CoffeeTypes & save in Db
        CoffeeType coffeeType = new CoffeeType();
        coffeeType.setTypeName("Coffee1");
        coffeeType.setPrice(3.0);
        coffeeType.setDisabled(DisabledFlag.N);
        coffeeType = coffeeTypeService.add(coffeeType);
        List<Integer> coffeeTypeIdList = new LinkedList<>();
        coffeeTypeIdList.add(coffeeType.getId());

        coffeeType.setId(0);
        coffeeType.setTypeName("Coffee2");
        coffeeType.setPrice(7.0);
        coffeeType = coffeeTypeService.add(coffeeType);
        coffeeTypeIdList.add(coffeeType.getId());

        // make CoffeeOrderItems & add to coffeeOrderItemList
        List<CoffeeOrderItem> coffeeOrderItemList = new LinkedList<>();
        CoffeeOrderItem coffeeOrderItem = new CoffeeOrderItem();
        coffeeOrderItem.setCoffeeTypeId(coffeeTypeIdList.get(0));
        coffeeOrderItem.setQuantity(4);
        coffeeOrderItemList.add(coffeeOrderItem);

        coffeeOrderItem = new CoffeeOrderItem();
        coffeeOrderItem.setCoffeeTypeId(coffeeTypeIdList.get(1));
        coffeeOrderItem.setQuantity(5);
        coffeeOrderItemList.add(coffeeOrderItem);

        //delete configuration if exist
        if (configurationService.get("n") != null) {
            configurationService.delete("n");
        }
        if (configurationService.get("x") != null) {
            configurationService.delete("x");
        }
        if (configurationService.get("m") != null) {
            configurationService.delete("m");
        }

        // calculate cost & make CoffeeOrder for default configuration
        // every 5(n) cups is free, delivery cost is 2(m); if order sum more then 10(x) - delivery is free
        // order is 4 cups with price 3 pear cup & 5 cups with price 7 pear cup
        CoffeeOrderAndCost coffeeOrder = coffeeOrderService.makeOrder("Petrov",
                "Street", coffeeOrderItemList);
        assertNotNull(coffeeOrder);
        assertEquals(3.0 * 4 + (7.0 * 4 + 0 * 1) ,
                coffeeOrder.getCoffeeOrder().getCost(), 0.00001);

        // delete CoffeeOrder & related to it CoffeeOrderItems
        int deletedRecords = coffeeOrderService.delete(coffeeOrder.getCoffeeOrder().getId());
        assertEquals(1, deletedRecords);
        CoffeeOrder coffeeOrderFromDB = coffeeOrderService.get(coffeeOrder.getCoffeeOrder().getId());
        assertNull(coffeeOrderFromDB);
        List<CoffeeOrderItem> allOrderItemsForOrderId =
                coffeeOrderItemService.getAllForOrderId(coffeeOrder.getCoffeeOrder().getId());
        assertTrue(allOrderItemsForOrderId.isEmpty());

        // make configuration & save in Db
        Configuration configuration = new Configuration();
        configuration.setId("n");
        configuration.setValue("30");  // every 30 cup of coffee is free
        configuration = configurationService.add(configuration);
        List<String> configurationIdList = new LinkedList<>();
        configurationIdList.add(configuration.getId());

        configuration.setId("x");
        configuration.setValue("1000");  // if total order Sum more then 1000 - delivery free
        configuration = configurationService.add(configuration);
        configurationIdList.add(configuration.getId());

        // calculate cost & make CoffeeOrder for given configuration (n = 30; x = 1000)
        // every 30(n) cups is free, delivery cost is 2(m); if order sum more then 1000(x) - delivery is free
        // order is 4 cups with price 3 pear cup & 5 cups with price 7 pear cup
        coffeeOrder = coffeeOrderService.makeOrder("Petrov",
                "Street", coffeeOrderItemList);
        assertNotNull(coffeeOrder);
        assertEquals(3.0 * 4 + 7.0 * 5 + 2.0 ,
                coffeeOrder.getCoffeeOrder().getCost(), 0.00001);

        // delete CoffeeOrder & related to it CoffeeOrderItems
        deletedRecords = coffeeOrderService.delete(coffeeOrder.getCoffeeOrder().getId());
        assertEquals(1, deletedRecords);
        coffeeOrderFromDB = coffeeOrderService.get(coffeeOrder.getCoffeeOrder().getId());
        assertNull(coffeeOrderFromDB);
        allOrderItemsForOrderId = coffeeOrderItemService.getAllForOrderId(coffeeOrder.getCoffeeOrder().getId());
        assertTrue(allOrderItemsForOrderId.isEmpty());

        // delete saved CoffeeTypes
        for (int coffeeTypeId : coffeeTypeIdList) {
            int deletedRow = coffeeTypeService.delete(coffeeTypeId);
            assertEquals(1, deletedRow);
            CoffeeType coffeeTypeFromDB = coffeeTypeService.get(coffeeTypeId);
            assertNull(coffeeTypeFromDB);
        }

        // delete saved Configuration
        for (String configurationId : configurationIdList) {
            int deletedRow = configurationService.delete(configurationId);
            assertEquals(1, deletedRow);
            Configuration configurationFromDB = configurationService.get(configurationId);
            assertNull(configurationFromDB);
        }
    }
}
