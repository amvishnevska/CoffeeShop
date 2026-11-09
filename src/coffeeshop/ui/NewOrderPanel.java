package coffeeshop.ui;

import coffeeshop.exception.BusinessException;
import coffeeshop.model.Customer;
import coffeeshop.model.Order;
import coffeeshop.model.OrderItem;
import coffeeshop.model.Product;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Page for creating an order: choose a customer, add products to the cart, place the order.
 */
public class NewOrderPanel extends JPanel implements Refreshable {

    private final MainFrame frame;
    private final JComboBox<Customer> customerBox = new JComboBox<>();
    private final JComboBox<Product> productBox = new JComboBox<>();
    private final JLabel productInfo = new JLabel(" ");
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JTextField commentField = new JTextField(30);
    private final ReadOnlyTableModel cartModel = new ReadOnlyTableModel("Product", "Unit price", "Quantity", "Total");
    private final JTable cartTable = UiUtils.table(cartModel);
    private final JLabel totalLabel = new JLabel();

    /** Items added to the cart but not yet ordered. */
    private final List<OrderItem> cart = new ArrayList<>();

    public NewOrderPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;

        FormPanel form = new FormPanel();
        form.addRow("Customer *", customerBox, "Who makes the order. New customers are added on the Customers page");
        form.addRow("Product", productBox, "Only products that are in stock are shown");
        form.addRow("", productInfo);
        JPanel qtyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        qtyRow.add(quantitySpinner);
        JButton addToCart = UiUtils.button("Add to cart", "Add the selected product with the given quantity");
        addToCart.addActionListener(e -> addToCart());
        qtyRow.add(addToCart);
        form.addRow("Quantity", qtyRow, "Number of items, 1-999");
        form.addRow("Comment", commentField, "Optional comment for delivery, up to 200 characters");
        productBox.addActionListener(e -> showProductInfo());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UiUtils.title("New order"), BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Cart"));
        add(cartScroll, BorderLayout.CENTER);

        JButton remove = UiUtils.button("Remove from cart", "Remove the selected line from the cart");
        remove.addActionListener(e -> removeFromCart());
        JButton clear = UiUtils.button("Clear cart", "Remove all products from the cart");
        clear.addActionListener(e -> {
            cart.clear();
            updateCart();
        });
        JButton place = UiUtils.button("Place order", "Create the order and decrease stock");
        place.addActionListener(e -> placeOrder());
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(remove);
        bottom.add(clear);
        bottom.add(place);
        bottom.add(totalLabel);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        Object selectedCustomer = customerBox.getSelectedItem();
        customerBox.removeAllItems();
        for (Customer c : frame.getShop().getCustomers()) {
            customerBox.addItem(c);
        }
        if (selectedCustomer != null) {
            customerBox.setSelectedItem(selectedCustomer);
        }

        productBox.removeAllItems();
        for (Product p : frame.getShop().getProducts()) {
            if (p.isAvailable()) {
                productBox.addItem(p);
            }
        }
        // remove products that were deleted from the catalog while they were in the cart
        cart.removeIf(item -> !frame.getShop().getProducts().contains(item.getProduct()));
        updateCart();
        showProductInfo();
    }

    private void showProductInfo() {
        Product p = (Product) productBox.getSelectedItem();
        productInfo.setText(p == null ? "No products in stock"
                : "Category: " + p.getCategory().getName() + ",  price: " + UiUtils.money(p.getSellingPrice())
                + ",  in stock: " + p.getStockQuantity());
    }

    private int quantityInCart(Product product) {
        int qty = 0;
        // if the product is already in the cart, only increase the quantity
        for (OrderItem item : cart) {
            if (item.getProduct() == product) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    private void addToCart() {
        Product p = (Product) productBox.getSelectedItem();
        if (p == null) {
            UiUtils.showError(this, "Please select a product.");
            return;
        }
        int qty = (Integer) quantitySpinner.getValue();
        int total = quantityInCart(p) + qty;
        if (!p.isAvailable(total)) {
            UiUtils.showError(this, "Not enough \"" + p.getName() + "\" in stock. Available: "
                    + p.getStockQuantity() + ", in cart: " + quantityInCart(p) + ".");
            return;
        }
        for (OrderItem item : cart) {
            if (item.getProduct() == p) {
                item.setQuantity(item.getQuantity() + qty);
                updateCart();
                return;
            }
        }
        cart.add(new OrderItem(p, qty));
        updateCart();
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            UiUtils.showInfo(this, "Please select a line in the cart first.");
            return;
        }
        cart.remove(row);
        updateCart();
    }

    private void updateCart() {
        cartModel.clear();
        double total = 0;
        for (OrderItem item : cart) {
            cartModel.addRow(new Object[]{item.getProduct().getName(), UiUtils.money(item.getUnitPrice()),
                    item.getQuantity(), UiUtils.money(item.getLineTotal())});
            total += item.getLineTotal();
        }
        totalLabel.setText("   Total: " + UiUtils.money(total));
    }

    private void placeOrder() {
        String comment = commentField.getText().trim();
        if (comment.length() > 200) {
            UiUtils.showError(this, "Comment must be at most 200 characters.");
            return;
        }
        try {
            Order order = frame.getShop().placeOrder((Customer) customerBox.getSelectedItem(), cart, comment);
            frame.saveData();
            UiUtils.showInfo(this, "Order #" + order.getId() + " created.\nTotal: " + UiUtils.money(order.getTotal()));
            cart.clear();
            commentField.setText("");
            refresh();
        } catch (BusinessException e) {
            UiUtils.showError(this, e.getMessage());
        }
    }
}
