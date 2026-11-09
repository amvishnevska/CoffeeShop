package coffeeshop.model;

import java.io.Serializable;

/** One line of an order: a product, its quantity and the price at the moment of purchase. */
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Product product;
    private int quantity;
    private final double unitPrice;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        this.product = product;
        this.unitPrice = product.getSellingPrice();
        setQuantity(quantity);
    }

   public double getLineTotal() {
        return unitPrice * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    @Override
    public String toString() {
        return String.format("%s x %d = %.2f EUR", product.getName(), quantity, getLineTotal());
    }
}
