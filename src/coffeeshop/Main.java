package coffeeshop;

import coffeeshop.exception.StorageException;
import coffeeshop.service.Shop;
import coffeeshop.storage.FileDataStorage;
import coffeeshop.ui.MainFrame;
import coffeeshop.ui.UiUtils;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point of the Coffee Shop application. */
public class Main {

    /** File where all data of the shop is stored (relative to the project folder). */
   private static final String DATA_FILE = "data/shop.dat";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
        // not critical: the default look and feel will be used
    }

    private static void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        Shop shop = new Shop(new FileDataStorage(DATA_FILE));
        String status;
        try {
            status = shop.loadOrCreate()
                    ? "Data loaded from " + DATA_FILE
                    : "First start: demo data created in " + DATA_FILE;
        } catch (StorageException e) {
            UiUtils.showError(null, e.getMessage() + "The application will start with demo data.");
            shop.resetToDemoData();
            status = "Data file was damaged, demo data is used.";
        }

        MainFrame frame = new MainFrame(shop);
        frame.setStatus(status);
        frame.setVisible(true);
    }
}
