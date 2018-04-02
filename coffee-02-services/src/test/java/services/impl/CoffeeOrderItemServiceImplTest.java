package services.impl;

import dao.ConfigurationDao;
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
import services.CoffeeOrderItemService;
import services.CoffeeOrderService;
import services.CoffeeTypeService;
import services.ConfigurationService;
import vo.CoffeeOrderAndCost;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@ContextConfiguration("/testContext-services.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CoffeeOrderItemServiceImplTest extends Assert {
    @Autowired
    private CoffeeOrderItemService coffeeOrderItemService;
    @Autowired
    private CoffeeTypeService coffeeTypeService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private CoffeeOrderService coffeeOrderService;

    @Test
    public void getAllForOrderId() throws SQLException {
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

        // check getAllForOrderId
        assertEquals(2,
                coffeeOrderItemService.getAllForOrderId(coffeeOrder.getCoffeeOrder().getId()).size());

        // delete CoffeeOrder & related to it CoffeeOrderItems
        int deletedRecords = coffeeOrderService.delete(coffeeOrder.getCoffeeOrder().getId());
        assertEquals(1, deletedRecords);
        CoffeeOrder coffeeOrderFromDB = coffeeOrderService.get(coffeeOrder.getCoffeeOrder().getId());
        assertNull(coffeeOrderFromDB);
        List<CoffeeOrderItem> allOrderItemsForOrderId =
                coffeeOrderItemService.getAllForOrderId(coffeeOrder.getCoffeeOrder().getId());
        assertTrue(allOrderItemsForOrderId.isEmpty());

        // delete saved CoffeeTypes
        for (int coffeeTypeId : coffeeTypeIdList) {
            int deletedRow = coffeeTypeService.delete(coffeeTypeId);
            assertEquals(1, deletedRow);
            CoffeeType coffeeTypeFromDB = coffeeTypeService.get(coffeeTypeId);
            assertNull(coffeeTypeFromDB);
        }
    }
}
