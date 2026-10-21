package coffeeshop.storage;

import coffeeshop.model.Category;
import coffeeshop.model.Customer;
import coffeeshop.model.Order;
import coffeeshop.model.Product;

import java.io.Serializable;

public class ShopData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Repository<Category> categories = new Repository<>();
    private final Repository<Product> products = new Repository<>();
    private final Repository<Customer> customers = new Repository<>();
    private final Repository<Order> orders = new Repository<>();

    public Repository<Category> getCategories() {
        return categories;
    }

    public Repository<Product> getProducts() {
        return products;
    }

    public Repository<Customer> getCustomers() {
        return customers;
    }

    public Repository<Order> getOrders() {
        return orders;
    }
}
