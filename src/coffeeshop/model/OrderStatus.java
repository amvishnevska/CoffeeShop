package coffeeshop.model;

/** Lifecycle status of an order. */
public enum OrderStatus {
    NEW("New"),
    PAID("Paid"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }

    @Override
    public String toString() {
        return title;
    }
}
