package services;

import entities.CoffeeOrder;
import entities.CoffeeOrderItem;
import vo.CoffeeOrderAndCost;

import java.io.Serializable;
import java.util.List;

public interface CoffeeOrderService {
    /**
     * Forms an order for not empty list of order elements and deliveryAddress != null,
     * calculates the cost of delivery and ordered coffee. If the formation is successful,
     * it saves the order and the list of order items in the database
     *
     * @param customerName determines the name of the customer
     * @param deliveryAddress determines the delivery address
     * @param coffeeOrderItemList list of order elements
     * @return an CoffeeOrderAndCost object that contains order stored in the database and object
     *         Cost that contains cost of delivery and ordered coffee,
     *         or return null if the order is not generated
     */
    CoffeeOrderAndCost makeOrder(String customerName, String deliveryAddress,
                                 List<CoffeeOrderItem> coffeeOrderItemList);

    /**
     * returns an CoffeeOrder record with an id = coffeeOrderId from the database
     *
     * @param coffeeOrderId determine id of CoffeeOrder record in database
     * @return CoffeeOrder record from the database with id = coffeeOrderId, or
     *         null if such an entity was not found
     */
    CoffeeOrder get(Serializable coffeeOrderId);

    /**
     * removes from the database an CoffeeOrder entity with an id = coffeeOrderId
     * and all CoffeeOrderItems where CoffeeOrderItem.orderId = coffeeOrderId
     *
     * @param coffeeOrderId determine id of entity in database
     * @return returns the number of deleted CoffeeOrder rows from the database
     */
    int delete(Serializable coffeeOrderId);
}
