package coffeeshop.ui;

import coffeeshop.model.Product;
import coffeeshop.service.Shop;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Start page: short statistics of the shop and quick navigation buttons.
 */
public class HomePanel extends JPanel implements Refreshable {

    private final MainFrame frame;
    private final JLabel categoriesLabel = new JLabel();
    private final JLabel productsLabel = new JLabel();
    private final JLabel customersLabel = new JLabel();
    private final JLabel ordersLabel = new JLabel();
    private final JLabel revenueLabel = new JLabel();
    private final JTextArea lowStockArea = new JTextArea(8, 40);

    public HomePanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;

        add(UiUtils.title("Welcome to Coffee Shop"), BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(5, 1, 5, 5));
        stats.setBorder(BorderFactory.createTitledBorder("Statistics"));
        stats.add(categoriesLabel);
        stats.add(productsLabel);
        stats.add(customersLabel);
        stats.add(ordersLabel);
        stats.add(revenueLabel);

        lowStockArea.setEditable(false);
        JScrollPane lowStock = new JScrollPane(lowStockArea);
        lowStock.setBorder(BorderFactory.createTitledBorder(
                "Low stock (less than " + Shop.LOW_STOCK_LIMIT + " items)"));

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        center.add(stats);
        center.add(lowStock);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(navButton("Product catalog", "Browse products by category", MainFrame.CATALOG));
        buttons.add(navButton("Categories", "Add, edit or delete categories", MainFrame.CATEGORIES));
        buttons.add(navButton("Customers", "Manage customers", MainFrame.CUSTOMERS));
        buttons.add(navButton("New order", "Create an order for a customer", MainFrame.NEW_ORDER));
        buttons.add(navButton("Orders", "View orders and change their status", MainFrame.ORDERS));
        add(buttons, BorderLayout.SOUTH);
    }

    private JButton navButton(String text, String hint, String page) {
        JButton b = UiUtils.button(text, hint);
        b.addActionListener(e -> frame.showPage(page));
        return b;
    }

    @Override
    public void refresh() {
        Shop shop = frame.getShop();
        categoriesLabel.setText("Categories: " + shop.getCategories().size());
        productsLabel.setText("Products: " + shop.getProducts().size());
        customersLabel.setText("Customers: " + shop.getCustomers().size());
        ordersLabel.setText("Orders: " + shop.getOrders().size());
        revenueLabel.setText("Revenue (without cancelled orders): " + UiUtils.money(shop.getTotalRevenue()));

        List<Product> low = shop.getLowStockProducts();
        StringBuilder sb = new StringBuilder();
        if (low.isEmpty()) {
            sb.append("All products are in stock.");
        }
        for (Product p : low) {
            sb.append(p.getName()).append(" - ").append(p.getStockQuantity()).append(" left\n");
        }
        lowStockArea.setText(sb.toString());
    }
}
