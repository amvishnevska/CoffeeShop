package coffeeshop.ui;

import coffeeshop.exception.BusinessException;
import coffeeshop.model.Order;
import coffeeshop.model.OrderItem;
import coffeeshop.model.OrderStatus;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Page with the list of orders: filter by status, order details and status change.
 */
public class OrdersPanel extends JPanel implements Refreshable {

    private static final String ALL = "All statuses";

    private final MainFrame frame;
    private final JComboBox<Object> filterBox = new JComboBox<>();
    private final ReadOnlyTableModel model = new ReadOnlyTableModel(
            "Order #", "Date", "Customer", "Items", "Total", "Status");
    private final JTable table = UiUtils.table(model);
    private final JTextArea detailsArea = new JTextArea(8, 40);
    private final JComboBox<OrderStatus> statusBox = new JComboBox<>(OrderStatus.values());
    private final List<Order> shownOrders = new ArrayList<>();

    public OrdersPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;

        filterBox.addItem(ALL);
        for (OrderStatus s : OrderStatus.values()) {
            filterBox.addItem(s);
        }
        filterBox.setToolTipText("Show only orders with this status");
        filterBox.addActionListener(e -> refresh());

        JPanel top = new JPanel(new BorderLayout());
        top.add(UiUtils.title("Orders"), BorderLayout.NORTH);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.add(new JLabel("Status:"));
        filter.add(filterBox);
        top.add(filter, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });
        detailsArea.setEditable(false);
        JScrollPane details = new JScrollPane(detailsArea);
        details.setBorder(BorderFactory.createTitledBorder("Order details"));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), details);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);

        statusBox.setToolTipText("New status for the selected order");
        JButton change = UiUtils.button("Change status",
                "Delivered and cancelled orders cannot be changed. Cancelling returns products to stock");
        change.addActionListener(e -> changeStatus());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("New status:"));
        bottom.add(statusBox);
        bottom.add(change);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        Object filter = filterBox.getSelectedItem();
        shownOrders.clear();
        model.clear();
        for (Order o : frame.getShop().getOrders()) {
            if (filter == ALL || o.getStatus() == filter) {
                shownOrders.add(o);
                model.addRow(new Object[]{o.getId(), o.getFormattedDate(), o.getCustomer().getFullName(),
                        o.getItemCount(), UiUtils.money(o.getTotal()), o.getStatus()});
            }
        }
        detailsArea.setText("");
    }

    private Order getSelected() {
        int row = table.getSelectedRow();
        return row < 0 ? null : shownOrders.get(row);
    }

    private void showDetails() {
        Order o = getSelected();
        if (o == null) {
            detailsArea.setText("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(o.getId()).append(" from ").append(o.getFormattedDate()).append('\n');
        sb.append("Customer: ").append(o.getCustomer()).append('\n');
        sb.append("Delivery address: ").append(o.getCustomer().getAddress()).append('\n');
        sb.append("Status: ").append(o.getStatus()).append('\n');
        if (!o.getComment().isEmpty()) {
            sb.append("Comment: ").append(o.getComment()).append('\n');
        }
        sb.append("\nItems:\n");
        for (OrderItem item : o.getItems()) {
            sb.append(String.format("  %-30s %3d x %8.2f = %8.2f EUR%n", item.getProduct().getName(),
                    item.getQuantity(), item.getUnitPrice(), item.getLineTotal()));
        }
        sb.append(String.format("%nTOTAL: %.2f EUR", o.getTotal()));
        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
        statusBox.setSelectedItem(o.getStatus());
    }

    private void changeStatus() {
        Order o = getSelected();
        if (o == null) {
            UiUtils.showInfo(this, "Please select an order in the table first.");
            return;
        }
        OrderStatus newStatus = (OrderStatus) statusBox.getSelectedItem();
        if (newStatus == OrderStatus.CANCELLED
                && !UiUtils.confirm(this, "Cancel order #" + o.getId() + "? Products will be returned to stock.")) {
            return;
        }
        try {
            frame.getShop().changeOrderStatus(o, newStatus);
            frame.saveData();
            frame.setStatus("Order #" + o.getId() + " status: " + o.getStatus());
            int row = table.getSelectedRow();
            refresh();
            if (row < table.getRowCount()) {
                table.setRowSelectionInterval(row, row);
            }
        } catch (BusinessException e) {
            UiUtils.showError(this, e.getMessage());
        }
    }
}
