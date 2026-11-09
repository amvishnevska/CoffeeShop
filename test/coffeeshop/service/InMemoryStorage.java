package coffeeshop.service;

import coffeeshop.storage.DataStorage;
import coffeeshop.storage.ShopData;

class InMemoryStorage implements DataStorage {

    private ShopData saved;
    private int saveCount = 0;

    @Override
    public void save(ShopData data) {
        saved = data;
        saveCount++;
    }

    @Override
    public ShopData load() {
        return saved;
    }

    @Override
    public boolean exists() {
        return saved != null;
    }

    int getSaveCount() {
        return saveCount;
    }
}
