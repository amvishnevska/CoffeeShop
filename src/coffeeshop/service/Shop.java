package coffeeshop.service;

import coffeeshop.exception.BusinessException;
import coffeeshop.exception.StorageException;
import coffeeshop.model.Category;
import coffeeshop.model.Coffee;
import coffeeshop.model.Customer;
import coffeeshop.model.Equipment;
import coffeeshop.model.Order;
import coffeeshop.model.OrderItem;
import coffeeshop.model.OrderStatus;
import coffeeshop.model.Product;
import coffeeshop.model.RoastLevel;
import coffeeshop.model.Tea;
import coffeeshop.model.TeaType;
import coffeeshop.storage.DataStorage;
import coffeeshop.storage.ShopData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic of the shop. The user interface works only through this class
 * and never changes the data directly.
 */
public class Shop {

    public static final int LOW_STOCK_LIMIT = 5;

    private final DataStorage storage;
    private ShopData data;

    public Shop(DataStorage storage) {
        this.storage = storage;
        this.data = new ShopData();
    }

    public boolean loadOrCreate() throws StorageException {
        if (storage.exists()) {
            data = storage.load();
            return true;
        }
        data = new ShopData();
        createDemoData();
        save();
        return false;
    }

    public void save() throws StorageException {
        storage.save(data);
    }

    public void resetToDemoData() {
        data = new ShopData();
        createDemoData();
    }

    public List<Category> getCategories() {
        return data.getCategories().getAll();
    }

    public Category addCategory(String name, String description) throws BusinessException {
        checkCategoryNameUnique(name, null);
        return data.getCategories().add(new Category(name, description));
    }

    public void updateCategory(Category category, String name, String description) throws BusinessException {
        checkCategoryNameUnique(name, category);
        category.setName(name);
        category.setDescription(description);
    }

    public void deleteCategory(Category category) throws BusinessException {
        int count = searchProducts(category).size();
        if (count > 0) {
            throw new BusinessException("Category \"" + category.getName() + "\" contains "
                    + count + " product(s). Move or delete them first.");
        }
        data.getCategories().remove(category.getId());
    }

    private void checkCategoryNameUnique(String name, Category current) throws BusinessException {
        for (Category c : getCategories()) {
            if (c != current && c.getName().equalsIgnoreCase(name.trim())) {
                throw new BusinessException("Category \"" + name.trim() + "\" already exists.");
            }
        }
    }

    public List<Product> getProducts() {
        return data.getProducts().getAll();
    }

    public Product addProduct(Product product) {
        return data.getProducts().add(product);
    }

    public void deleteProduct(Product product) {
        data.getProducts().remove(product.getId());
    }

    public List<Product> searchProducts(String text) {
        return searchProducts(text, null, 0, Double.MAX_VALUE);
    }

    public List<Product> searchProducts(Category category) {
        return searchProducts("", category, 0, Double.MAX_VALUE);
    }

    public List<Product> searchProducts(double minPrice, double maxPrice) {
        return searchProducts("", null, minPrice, maxPrice);
    }

    public List<Product> searchProducts(String text, Category category, double minPrice, double maxPrice) {
        String query = text == null ? "" : text.trim().toLowerCase();
        List<Product> result = new ArrayList<>();
        for (Product p : getProducts()) {
            boolean textOk = query.isEmpty()
                    || p.getName().toLowerCase().contains(query)
                    || p.getBrand().toLowerCase().contains(query);
            boolean categoryOk = category == null || p.getCategory() == category;
            boolean priceOk = p.getSellingPrice() >= minPrice && p.getSellingPrice() <= maxPrice;
            if (textOk && categoryOk && priceOk) {
                result.add(p);
            }
        }
        return result;
    }

     public List<Customer> getCustomers() {
        return data.getCustomers().getAll();
    }

    public Customer addCustomer(Customer customer) throws BusinessException {
        checkEmailUnique(customer.getEmail(), null);
        return data.getCustomers().add(customer);
    }

    public void checkEmailUnique(String email, Customer current) throws BusinessException {
        for (Customer c : getCustomers()) {
            if (c != current && c.getEmail().equalsIgnoreCase(email.trim())) {
                throw new BusinessException("Customer with e-mail " + email.trim() + " already exists.");
            }
        }
    }

    public void deleteCustomer(Customer customer) throws BusinessException {
        if (!getOrdersOf(customer).isEmpty()) {
            throw new BusinessException("Customer \"" + customer.getFullName()
                    + "\" has orders and cannot be deleted.");
        }
        data.getCustomers().remove(customer.getId());
    }

    public List<Order> getOrders() {
        return data.getOrders().getAll();
    }

    public List<Order> getOrdersOf(Customer customer) {
        List<Order> result = new ArrayList<>();
        for (Order o : getOrders()) {
            if (o.getCustomer() == customer) {
                result.add(o);
            }
        }
        return result;
    }

    public Order placeOrder(Customer customer, List<OrderItem> items, String comment) throws BusinessException {
        if (customer == null) {
            throw new BusinessException("Please select a customer.");
        }
        if (items == null || items.isEmpty()) {
            throw new BusinessException("The cart is empty. Add at least one product.");
        }
        Map<Product, Integer> needed = new HashMap<>();
        for (OrderItem item : items) {
            needed.merge(item.getProduct(), item.getQuantity(), Integer::sum);
        }
        for (Map.Entry<Product, Integer> entry : needed.entrySet()) {
            Product p = entry.getKey();
            if (data.getProducts().findById(p.getId()) != p) {
                throw new BusinessException("Product \"" + p.getName() + "\" is no longer sold.");
            }
            if (!p.isAvailable(entry.getValue())) {
                throw new BusinessException("Not enough \"" + p.getName() + "\" in stock. Available: "
                        + p.getStockQuantity() + ", requested: " + entry.getValue() + ".");
            }
        }
        for (Map.Entry<Product, Integer> entry : needed.entrySet()) {
            entry.getKey().decreaseStock(entry.getValue());
        }
        return data.getOrders().add(new Order(customer, items, comment));
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus) throws BusinessException {
        if (order.getStatus().isFinal()) {
            throw new BusinessException("Order #" + order.getId() + " is already "
                    + order.getStatus() + " and cannot be changed.");
        }
        if (newStatus == order.getStatus()) {
            return;
        }
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                item.getProduct().increaseStock(item.getQuantity());
            }
        }
        order.setStatus(newStatus);
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Order o : getOrders()) {
            if (o.getStatus() != OrderStatus.CANCELLED) {
                total += o.getTotal();
            }
        }
        return total;
    }

    public List<Product> getLowStockProducts() {
        List<Product> result = new ArrayList<>();
        for (Product p : getProducts()) {
            if (p.getStockQuantity() < LOW_STOCK_LIMIT) {
                result.add(p);
            }
        }
        return result;
    }

    private void createDemoData() {
        Category beans = data.getCategories().add(new Category("Coffee beans", "Whole bean coffee from different countries"));
        Category ground = data.getCategories().add(new Category("Ground coffee", "Ready to brew ground coffee"));
        Category tea = data.getCategories().add(new Category("Tea", "Black, green and herbal tea"));
        Category machines = data.getCategories().add(new Category("Coffee machines", "Espresso machines and grinders"));
        Category accessories = data.getCategories().add(new Category("Accessories", "Cups, kettles, filters"));

        Coffee ethiopia = new Coffee("Ethiopia Yirgacheffe", "Floral and citrus notes", 14.90, 25,
                beans, "Roast Lab", RoastLevel.LIGHT, "Ethiopia", 250, false);
        ethiopia.setRating(4.8);
        data.getProducts().add(ethiopia);
        Coffee brazil = new Coffee("Brazil Santos", "Chocolate and nuts", 11.50, 40,
                beans, "Roast Lab", RoastLevel.MEDIUM, "Brazil", 500, false);
        brazil.setDiscountPercent(10);
        brazil.setRating(4.3);
        data.getProducts().add(brazil);
        data.getProducts().add(new Coffee("Italian Espresso", "Strong dark roast for espresso", 8.90, 3,
                ground, "Lavazza", RoastLevel.DARK, "Blend", 250, true));
        data.getProducts().add(new Coffee("Colombia Supremo", "Balanced, caramel sweetness", 9.70, 18,
                ground, "Jacobs", RoastLevel.MEDIUM_DARK, "Colombia", 250, true));

        data.getProducts().add(new Tea("Earl Grey", "Black tea with bergamot", 5.20, 30,
                tea, "Twinings", TeaType.BLACK, 100, true));
        data.getProducts().add(new Tea("Sencha", "Japanese green tea", 7.80, 12,
                tea, "Matcha House", TeaType.GREEN, 100, false));

        Equipment machine = new Equipment("Espresso machine EC685", "15 bar pump, milk frother", 249.00, 4,
                machines, "DeLonghi", 24, "Stainless steel", 1300);
        machine.setDiscountPercent(15);
        data.getProducts().add(machine);
        data.getProducts().add(new Equipment("Burr grinder", "40 grind settings", 89.99, 7,
                machines, "Baratza", 12, "Plastic, steel", 150));
        data.getProducts().add(new Equipment("Ceramic cup 300 ml", "Double wall cup", 12.00, 50,
                accessories, "Hario", 0, "Ceramic", 0));
        data.getProducts().add(new Equipment("Gooseneck kettle", "Pour-over kettle 1 l", 39.90, 9,
                accessories, "Hario", 12, "Stainless steel", 0));

        Customer anna = data.getCustomers().add(new Customer("Anna Ozola", "anna.ozola@mail.com",
                "+371 26123456", "Riga, Brivibas iela 10"));
        data.getCustomers().add(new Customer("John Smith", "john.smith@mail.com",
                "+44 7700900123", "London, Baker Street 221B"));

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(ethiopia, 2));
        items.add(new OrderItem(brazil, 1));
        ethiopia.decreaseStock(2);
        brazil.decreaseStock(1);
        data.getOrders().add(new Order(anna, items, "Please deliver after 18:00"));
    }
}
