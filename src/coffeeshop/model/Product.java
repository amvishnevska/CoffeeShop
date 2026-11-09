package coffeeshop.model;

import java.io.Serializable;
import java.time.LocalDate;

/** Abstract base class for all goods sold in the shop. */
public abstract class Product implements Identifiable, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private Category category;
    private String brand;
    private LocalDate dateAdded;
    private double rating;

    protected Product(String name, String description, double price, int stockQuantity,
                      Category category, String brand) {
        setName(name);
        setDescription(description);
        setPrice(price);
        setStockQuantity(stockQuantity);
        setCategory(category);
        setBrand(brand);
        this.dateAdded = LocalDate.now();
        this.rating = 0;
    }

    public abstract ProductType getType();

    /** Returns the text with attributes specific to the subclass. */
    public abstract String getSpecificDetails();

    public String getFullDetails() {
        return "Name: " + name + "\n"
                + "Type: " + getType() + "\n"
                + "Category: " + (category == null ? "-" : category.getName()) + "\n"
                + "Brand: " + brand + "\n"
                + String.format("Price: %.2f EUR%n", price)
                + String.format("Selling price: %.2f EUR%n", getSellingPrice())
                + "In stock: " + stockQuantity + "\n"
                + "Rating: " + rating + " / 5\n"
                + "Added: " + dateAdded + "\n"
                + "Description: " + description + "\n"
                + getSpecificDetails();
    }

    /** Price the customer actually pays. */
    public double getSellingPrice() {
        return price;
    }

    public boolean isAvailable(int quantity) {
        return quantity > 0 && quantity <= stockQuantity;
    }

    public boolean isAvailable() {
        return isAvailable(1);
    }

    /** Decreases stock after an order. */
    public void decreaseStock(int quantity) {
        if (!isAvailable(quantity)) {
            throw new IllegalArgumentException("Not enough items in stock: " + name);
        }
        stockQuantity -= quantity;
    }

    /** Returns items to stock (e.g. when an order is cancelled). */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stockQuantity += quantity;
    }

    // ---------- getters and setters ----------
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must be selected");
        }
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? "" : brand.trim();
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f EUR)", name, getSellingPrice());
    }
}
