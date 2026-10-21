package coffeeshop.storage;

import coffeeshop.exception.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Stores shop data in a binary file using Java object serialization.
 */
public class FileDataStorage implements DataStorage {

    private final File file;

    public FileDataStorage(String filePath) {
        this.file = new File(filePath);
    }

    @Override
    public void save(ShopData data) throws StorageException {
        File folder = file.getAbsoluteFile().getParentFile();
        if (folder != null && !folder.exists() && !folder.mkdirs()) {
            throw new StorageException("Cannot create folder " + folder, null);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new StorageException("Cannot save data to " + file.getPath(), e);
        }
    }

    @Override
    public ShopData load() throws StorageException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ShopData) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new StorageException("Cannot read data from " + file.getPath()
                    + ". The file is missing or damaged.", e);
        }
    }

    @Override
    public boolean exists() {
        return file.isFile();
    }

    public String getPath() {
        return file.getAbsolutePath();
    }
}
