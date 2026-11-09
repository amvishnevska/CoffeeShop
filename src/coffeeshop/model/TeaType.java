package coffeeshop.model;

/** Type of tea. */
public enum TeaType {
    BLACK("Black"),
    GREEN("Green"),
    WHITE("White"),
    OOLONG("Oolong"),
    HERBAL("Herbal");

    private final String title;

    TeaType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
