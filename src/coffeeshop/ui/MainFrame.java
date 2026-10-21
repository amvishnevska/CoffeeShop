package coffeeshop.ui;

import coffeeshop.exception.StorageException;
import coffeeshop.service.Shop;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main window of the application. Contains the menu bar and switches
 * between pages (home, catalog, categories, customers, new order, orders).
 */
public class MainFrame extends JFrame {

    public static final String HOME = "home";
    public static final String CATALOG = "catalog";
    public static final String CATEGORIES = "categories";
    public static final String CUSTOMERS = "customers";
    public static final String NEW_ORDER = "newOrder";
    public static final String ORDERS = "orders";

    private final Shop shop;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, Refreshable> pages = new LinkedHashMap<>();
    private final JLabel statusBar = new JLabel(" ");

    public MainFrame(Shop shop) {
        super("Coffee Shop - online store catalog");
        this.shop = shop;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        addPage(HOME, new HomePanel(this));
        addPage(CATALOG, new CatalogPanel(this));
        addPage(CATEGORIES, new CategoriesPanel(this));
        addPage(CUSTOMERS, new CustomersPanel(this));
        addPage(NEW_ORDER, new NewOrderPanel(this));
        addPage(ORDERS, new OrdersPanel(this));

        setJMenuBar(createMenuBar());
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(content, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        setSize(1100, 650);
        setLocationRelativeTo(null);
        showPage(HOME);
    }

    private void addPage(String name, JPanel page) {
        content.add(page, name);
        pages.put(name, (Refreshable) page);
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(menuItem("Home", KeyEvent.VK_H, () -> showPage(HOME)));
        file.add(menuItem("Save", KeyEvent.VK_S, this::saveData));
        file.addSeparator();
        file.add(menuItem("Exit", KeyEvent.VK_Q, this::exitApplication));
        bar.add(file);

        JMenu catalog = new JMenu("Catalog");
        catalog.add(menuItem("Products", KeyEvent.VK_P, () -> showPage(CATALOG)));
        catalog.add(menuItem("Categories", KeyEvent.VK_K, () -> showPage(CATEGORIES)));
        bar.add(catalog);

        JMenu customers = new JMenu("Customers");
        customers.add(menuItem("Customer list", KeyEvent.VK_U, () -> showPage(CUSTOMERS)));
        bar.add(customers);

        JMenu orders = new JMenu("Orders");
        orders.add(menuItem("New order", KeyEvent.VK_N, () -> showPage(NEW_ORDER)));
        orders.add(menuItem("Order list", KeyEvent.VK_O, () -> showPage(ORDERS)));
        bar.add(orders);

        JMenu help = new JMenu("Help");
        help.add(menuItem("About", KeyEvent.VK_F1, this::showAbout));
        bar.add(help);
        return bar;
    }

    private JMenuItem menuItem(String text, int key, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setAccelerator(key == KeyEvent.VK_F1
                ? KeyStroke.getKeyStroke(key, 0)
                : KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> action.run());
        return item;
    }

    public void showPage(String name) {
        pages.get(name).refresh();
        cardLayout.show(content, name);
    }

    public boolean saveData() {
        try {
            shop.save();
            setStatus("Data saved.");
            return true;
        } catch (StorageException e) {
            UiUtils.showError(this, e.getMessage());
            setStatus("Error: data was NOT saved.");
            return false;
        }
    }

    public void setStatus(String text) {
        statusBar.setText(text);
    }

    public Shop getShop() {
        return shop;
    }

    private void showAbout() {
        UiUtils.showInfo(this, "Coffee Shop - online store product catalog\n"
                + "Course project, Object-Oriented Programming\n\n"
                + "Features:\n"
                + " - product catalog by categories, search and filter\n"
                + " - categories, products and customers management\n"
                + " - creating orders and tracking their status\n"
                + " - data is stored in the file data/shop.dat");
    }

    private void exitApplication() {
        if (!saveData() && !UiUtils.confirm(this, "Data could not be saved. Exit anyway?")) {
            return;
        }
        dispose();
        System.exit(0);
    }
}
