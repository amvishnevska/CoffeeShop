package coffeeshop.ui;

import coffeeshop.exception.ValidationException;
import coffeeshop.model.Category;
import coffeeshop.model.Discountable;
import coffeeshop.model.Product;
import coffeeshop.service.Shop;
import coffeeshop.util.Validator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CatalogPanel extends JPanel implements Refreshable {

    private static final String ALL_CATEGORIES = "All categories";

    private final MainFrame frame;
    private final DefaultListModel<Object> categoryModel = new DefaultListModel<>();
    private final JList<Object> categoryList = new JList<>(categoryModel);
    private final ReadOnlyTableModel tableModel = new ReadOnlyTableModel(
            "ID", "Name", "Type", "Category", "Brand", "Price", "Discount", "Selling price", "In stock", "Rating");
    private final JTable table = UiUtils.table(tableModel);
    private final JTextField searchField = new JTextField(15);
    private final JTextField minPriceField = new JTextField(6);
    private final JTextField maxPriceField = new JTextField(6);
    private final JLabel countLabel = new JLabel();

    /** Products currently shown in the table; row index = list index. */
    private final List<Product> shownProducts = new ArrayList<>();

    public CatalogPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;

        JPanel top = new JPanel(new BorderLayout());
        top.add(UiUtils.title("Product catalog"), BorderLayout.NORTH);
        top.add(createFilterBar(), BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setToolTipText("Select a category to see its products");
        categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                applyFilter();
            }
        });
        JScrollPane categoryScroll = new JScrollPane(categoryList);
        categoryScroll.setBorder(BorderFactory.createTitledBorder("Categories"));
        categoryScroll.setPreferredSize(new Dimension(200, 400));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDetails();
                }
            }
        });
        // column widths, so that names are not cut
        int[] widths = {35, 210, 75, 120, 95, 80, 60, 90, 60, 50};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Products (double click to see details)"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryScroll, tableScroll);
        add(split, BorderLayout.CENTER);
        add(createButtonBar(), BorderLayout.SOUTH);
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField.setToolTipText("Part of product name or brand, e.g. \"espresso\"");
        minPriceField.setToolTipText("Minimal selling price in EUR, leave empty for no limit");
        maxPriceField.setToolTipText("Maximal selling price in EUR, leave empty for no limit");
        bar.add(new JLabel("Search:"));
        bar.add(searchField);
        bar.add(new JLabel("Price from:"));
        bar.add(minPriceField);
        bar.add(new JLabel("to:"));
        bar.add(maxPriceField);

        JButton search = UiUtils.button("Search", "Apply search and price filter");
        search.addActionListener(e -> applyFilter());
        searchField.addActionListener(e -> applyFilter()); // Enter key starts search
        JButton reset = UiUtils.button("Reset", "Clear all filters");
        reset.addActionListener(e -> {
            searchField.setText("");
            minPriceField.setText("");
            maxPriceField.setText("");
            categoryList.setSelectedIndex(0);
            applyFilter();
        });
        bar.add(search);
        bar.add(reset);
        bar.add(countLabel);
        return bar;
    }

    private JPanel createButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = UiUtils.button("Add product", "Add a new product to the catalog");
        add.addActionListener(e -> addProduct());
        JButton edit = UiUtils.button("Edit", "Edit the selected product");
        edit.addActionListener(e -> editProduct());
        JButton delete = UiUtils.button("Delete", "Delete the selected product");
        delete.addActionListener(e -> deleteProduct());
        JButton details = UiUtils.button("Details", "Show all information about the selected product");
        details.addActionListener(e -> showDetails());
        bar.add(add);
        bar.add(edit);
        bar.add(delete);
        bar.add(details);
        return bar;
    }

    @Override
    public void refresh() {
        Object selected = categoryList.getSelectedValue();
        categoryModel.clear();
        categoryModel.addElement(ALL_CATEGORIES);
        for (Category c : frame.getShop().getCategories()) {
            categoryModel.addElement(c);
        }
        if (selected != null && categoryModel.contains(selected)) {
            categoryList.setSelectedValue(selected, true);
        } else {
            categoryList.setSelectedIndex(0);
        }
        applyFilter();
    }

    /** Reads filter fields, searches products and fills the table. */
    private void applyFilter() {
        double min;
        double max;
        try {
            min = minPriceField.getText().trim().isEmpty()
                    ? 0 : Validator.parseDouble(minPriceField.getText(), "Price from", 0, 100000);
            max = maxPriceField.getText().trim().isEmpty()
                    ? Double.MAX_VALUE : Validator.parseDouble(maxPriceField.getText(), "Price to", 0, 100000);
            if (min > max) {
                throw new ValidationException("\"Price from\" cannot be greater than \"Price to\".");
            }
        } catch (ValidationException e) {
            UiUtils.showError(this, e.getMessage());
            return;
        }
        Object selected = categoryList.getSelectedValue();
        Category category = selected instanceof Category ? (Category) selected : null;
        fillTable(frame.getShop().searchProducts(searchField.getText(), category, min, max));
    }

    private void fillTable(List<Product> products) {
        shownProducts.clear();
        shownProducts.addAll(products);
        tableModel.clear();
        for (Product p : products) {
            String discount = p instanceof Discountable ? ((Discountable) p).getDiscountPercent() + "%" : "-";
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getType(), p.getCategory().getName(), p.getBrand(),
                    UiUtils.money(p.getPrice()), discount, UiUtils.money(p.getSellingPrice()),
                    p.getStockQuantity(), p.getRating()
            });
        }
        countLabel.setText("Found: " + products.size());
    }

    /** @return selected product or null (with a message to the user) */
    private Product getSelectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtils.showInfo(this, "Please select a product in the table first.");
            return null;
        }
        return shownProducts.get(row);
    }

    private void addProduct() {
        Shop shop = frame.getShop();
        if (shop.getCategories().isEmpty()) {
            UiUtils.showError(this, "Create at least one category first (Catalog -> Categories).");
            return;
        }
        ProductDialog dialog = new ProductDialog(frame, shop, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            frame.saveData();
            frame.setStatus("Product \"" + dialog.getProduct().getName() + "\" added.");
            applyFilter();
        }
    }

    private void editProduct() {
        Product product = getSelectedProduct();
        if (product == null) {
            return;
        }
        ProductDialog dialog = new ProductDialog(frame, frame.getShop(), product);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            frame.saveData();
            frame.setStatus("Product \"" + product.getName() + "\" updated.");
            applyFilter();
        }
    }

    private void deleteProduct() {
        Product product = getSelectedProduct();
        if (product == null) {
            return;
        }
        if (UiUtils.confirm(this, "Delete product \"" + product.getName() + "\"?")) {
            frame.getShop().deleteProduct(product);
            frame.saveData();
            frame.setStatus("Product \"" + product.getName() + "\" deleted.");
            applyFilter();
        }
    }

    private void showDetails() {
        Product product = getSelectedProduct();
        if (product == null) {
            return;
        }
        JTextArea text = new JTextArea(product.getFullDetails());
        text.setEditable(false);
        text.setColumns(40);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(text),
                "Product details", JOptionPane.INFORMATION_MESSAGE);
    }
}
