package coffeeshop.service;

import coffeeshop.exception.BusinessException;
import coffeeshop.model.Category;
import coffeeshop.model.Customer;
import coffeeshop.model.Equipment;
import coffeeshop.model.Order;
import coffeeshop.model.OrderItem;
import coffeeshop.model.OrderStatus;
import coffeeshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShopOrderTest {

    private Shop shop;
    private Customer customer;
    private Product machine;

    @BeforeEach
    void setUp() throws BusinessException {
        shop = new Shop(new InMemoryStorage());
        Category category = shop.addCategory("Coffee machines", "");
        machine = shop.addProduct(new Equipment("Espresso machine EC685", "", 249.00, 4,
                category, "DeLonghi", 24, "Steel", 1300));
        customer = shop.addCustomer(new Customer("Anna Ozola", "anna.ozola@mail.com", "", "Riga"));
    }

    private List<OrderItem> cart(int... quantities) {
        List<OrderItem> items = new ArrayList<>();
        for (int q : quantities) {
            items.add(new OrderItem(machine, q));
        }
        return items;
    }

    @Test
    void orderDecreasesStock() throws BusinessException {
        Order order = shop.placeOrder(customer, cart(2), "");

        assertEquals(2, machine.getStockQuantity());
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(498.00, order.getTotal(), 0.001);
        assertEquals(1, shop.getOrders().size());
    }

    @Test
    void lastItemsCanBeOrdered() throws BusinessException {
        shop.placeOrder(customer, cart(4), "");

        assertEquals(0, machine.getStockQuantity());
    }

    @Test
    void notEnoughStockCreatesNoOrder() {
        assertThrows(BusinessException.class, () -> shop.placeOrder(customer, cart(5), ""));

        assertEquals(4, machine.getStockQuantity(), "stock must not change");
        assertEquals(0, shop.getOrders().size(), "no order must be created");
    }

    @Test
    void quantitiesOfTheSameProductAreSummed() {
        // 3 + 2 = 5 items, but only 4 are in stock
        assertThrows(BusinessException.class, () -> shop.placeOrder(customer, cart(3, 2), ""));

        assertEquals(4, machine.getStockQuantity());
        assertEquals(0, shop.getOrders().size());
    }

    @Test
    void emptyCartCreatesNoOrder() {
        assertThrows(BusinessException.class, () -> shop.placeOrder(customer, cart(), ""));
        assertEquals(0, shop.getOrders().size());
    }

    @Test
    void orderWithoutCustomerCreatesNoOrder() {
        assertThrows(BusinessException.class, () -> shop.placeOrder(null, cart(1), ""));
        assertEquals(4, machine.getStockQuantity());
    }

    @Test
    void cancellingReturnsItemsToStock() throws BusinessException {
        Order order = shop.placeOrder(customer, cart(3), "");

        shop.changeOrderStatus(order, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(4, machine.getStockQuantity());
    }

    @Test
    void cancelledOrderCannotBeChanged() throws BusinessException {
        Order order = shop.placeOrder(customer, cart(1), "");
        shop.changeOrderStatus(order, OrderStatus.CANCELLED);

        assertThrows(BusinessException.class, () -> shop.changeOrderStatus(order, OrderStatus.PAID));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(4, machine.getStockQuantity(), "stock must not be returned twice");
    }
}
