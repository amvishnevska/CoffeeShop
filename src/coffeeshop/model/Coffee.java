package coffeeshop.model;

public class Coffee extends Product implements Discountable {

    private static final long serialVersionUID = 1L;

    private RoastLevel roastLevel;
    private String origin;
    private int weightGrams;
    private boolean ground;
    private int discountPercent;

    public Coffee(String name, String description, double price, int stockQuantity,
                  Category category, String brand,
                  RoastLevel roastLevel, String origin, int weightGrams, boolean ground) {
        super(name, description, price, stockQuantity, category, brand);
        setRoastLevel(roastLevel);
        setOrigin(origin);
        setWeightGrams(weightGrams);
        setGround(ground);
    }

    @Override
    public ProductType getType() {
        return ProductType.COFFEE;
    }

    @Override
    public String getSpecificDetails() {
        return "Roast level: " + roastLevel + "\n"
                + "Origin: " + origin + "\n"
                + "Weight: " + weightGrams + " g\n"
                + "Form: " + (ground ? "ground" : "beans") + "\n"
                + "Discount: " + discountPercent + "%";
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

    public RoastLevel getRoastLevel() {
        return roastLevel;
    }

    public void setRoastLevel(RoastLevel roastLevel) {
        if (roastLevel == null) {
            throw new IllegalArgumentException("Roast level must be selected");
        }
        this.roastLevel = roastLevel;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin == null ? "" : origin.trim();
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(int weightGrams) {
        if (weightGrams <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        this.weightGrams = weightGrams;
    }

    public boolean isGround() {
        return ground;
    }

    public void setGround(boolean ground) {
        this.ground = ground;
    }
}
