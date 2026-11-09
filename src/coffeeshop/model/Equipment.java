package coffeeshop.model;

/** Coffee equipment: grinders, coffee machines, kettles, cups etc. */
public class Equipment extends Product implements Discountable {

    private static final long serialVersionUID = 1L;

    private int warrantyMonths;
    private String material;
    private int powerWatts;
    private int discountPercent;

    public Equipment(String name, String description, double price, int stockQuantity,
                     Category category, String brand,
                     int warrantyMonths, String material, int powerWatts) {
        super(name, description, price, stockQuantity, category, brand);
        setWarrantyMonths(warrantyMonths);
        setMaterial(material);
        setPowerWatts(powerWatts);
    }

    @Override
    public ProductType getType() {
        return ProductType.EQUIPMENT;
    }

    @Override
    public String getSpecificDetails() {
        return "Warranty: " + warrantyMonths + " months\n"
                + "Material: " + material + "\n"
                + "Power: " + (isElectric() ? powerWatts + " W" : "not electric") + "\n"
                + "Discount: " + discountPercent + "%";
    }

   public boolean isElectric() {
        return powerWatts > 0;
    }

    @Override
    public double getSellingPrice() {
        return getDiscountedPrice();
    }

    @Override
    public double getDiscountedPrice() {
        return getPrice() * (100 - discountPercent) / 100.0;
    }

    @Override
    public int getDiscountPercent() {
        return discountPercent;
    }

    @Override
    public void setDiscountPercent(int percent) {
        if (percent < 0 || percent > MAX_DISCOUNT) {
            throw new IllegalArgumentException("Discount must be between 0 and " + MAX_DISCOUNT);
        }
        this.discountPercent = percent;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        if (warrantyMonths < 0) {
            throw new IllegalArgumentException("Warranty cannot be negative");
        }
        this.warrantyMonths = warrantyMonths;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material == null ? "" : material.trim();
    }

    public int getPowerWatts() {
        return powerWatts;
    }

    public void setPowerWatts(int powerWatts) {
        if (powerWatts < 0) {
            throw new IllegalArgumentException("Power cannot be negative");
        }
        this.powerWatts = powerWatts;
    }
}
