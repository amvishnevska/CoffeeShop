package coffeeshop.storage;

import coffeeshop.model.Identifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repository<T extends Identifiable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<T> items = new ArrayList<>();
    private int nextId = 1;

    public T add(T item) {
        item.setId(nextId++);
        items.add(item);
        return item;
    }

    public boolean remove(int id) {
        return items.removeIf(item -> item.getId() == id);
    }

    public T findById(int id) {
        for (T item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }
}
