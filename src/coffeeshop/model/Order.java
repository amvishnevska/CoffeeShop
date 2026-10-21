package coffeeshop.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Identifiable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private int id;
    private final Customer customer;
    private final List<OrderItem> items = new ArrayList<>();
    private final LocalDateTime orderDate;
    private OrderStatus status;
    private String comment;

    public Order(Customer customer, List<OrderItem> items, String comment) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer must be selected");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        this.customer = customer;
        this.items.addAll(items);
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.NEW;
        this.comment = comment == null ? "" : comment.trim();
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (OrderItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getFormattedDate() {
        return orderDate.format(DATE_FORMAT);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Order #" + id + " (" + customer.getFullName() + ")";
    }
}
