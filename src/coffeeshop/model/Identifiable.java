package coffeeshop.model;

/** Object with a unique id that can be stored in a repository. */
public interface Identifiable {

    int getId();

    /** Sets the identifier. */
    void setId(int id);
}
