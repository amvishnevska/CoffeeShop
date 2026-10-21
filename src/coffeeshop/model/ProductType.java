package coffeeshop.model;

public enum ProductType {
    COFFEE("Coffee"),
    TEA("Tea"),
    EQUIPMENT("Equipment");

    private final String title;

    ProductType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
