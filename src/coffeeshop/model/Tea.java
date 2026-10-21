package coffeeshop.model;

public class Tea extends Product {

    private static final long serialVersionUID = 1L;

    private TeaType teaType;
    private int weightGrams;
    private boolean bagged;

    public Tea(String name, String description, double price, int stockQuantity,
               Category category, String brand,
               TeaType teaType, int weightGrams, boolean bagged) {
        super(name, description, price, stockQuantity, category, brand);
        setTeaType(teaType);
        setWeightGrams(weightGrams);
        setBagged(bagged);
    }

    @Override
    public ProductType getType() {
        return ProductType.TEA;
    }

    @Override
    public String getSpecificDetails() {
        return "Tea type: " + teaType + "\n"
                + "Weight: " + weightGrams + " g\n"
                + "Packing: " + (bagged ? "tea bags" : "loose leaf");
    }

    public TeaType getTeaType() {
        return teaType;
    }

    public void setTeaType(TeaType teaType) {
        if (teaType == null) {
            throw new IllegalArgumentException("Tea type must be selected");
        }
        this.teaType = teaType;
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

    public boolean isBagged() {
        return bagged;
    }

    public void setBagged(boolean bagged) {
        this.bagged = bagged;
    }
}
