package coffeeshop.model;

public enum RoastLevel {
    LIGHT("Light"),
    MEDIUM("Medium"),
    MEDIUM_DARK("Medium-dark"),
    DARK("Dark");

    private final String title;

    RoastLevel(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
