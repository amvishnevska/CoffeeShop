package coffeeshop.model;

import java.io.Serializable;

/** Product category; one category contains many products. */
public class Category implements Identifiable, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String description;

    public Category(String name, String description) {
        setName(name);
        setDescription(description);
    }

    /** Name is shown in combo boxes and lists. */
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
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    @Override
    public String toString() {
        return name;
    }
}
