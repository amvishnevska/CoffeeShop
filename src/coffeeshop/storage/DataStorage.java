package coffeeshop.storage;

import coffeeshop.exception.StorageException;

/**
 * Contract for saving and loading shop data.
 * Allows to replace the file storage with, for example, a database without changing other code.
 */
public interface DataStorage {

    /** Saves all data. */
    void save(ShopData data) throws StorageException;

    /** Loads all data. */
    ShopData load() throws StorageException;

    boolean exists();
}
