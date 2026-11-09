package coffeeshop.model;

/** Kind of product, used by the user interface to choose a subclass of {@link Product}. */
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
