package coffeeshop.ui;

import coffeeshop.exception.BusinessException;
import coffeeshop.model.Customer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Page for managing customers.
 */
public class CustomersPanel extends JPanel implements Refreshable {

    private final MainFrame frame;
    private final ReadOnlyTableModel model = new ReadOnlyTableModel(
            "ID", "Full name", "E-mail", "Phone", "Address", "Orders");
    private final JTable table = UiUtils.table(model);

    public CustomersPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;

        add(UiUtils.title("Customers"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = UiUtils.button("Add customer", "Register a new customer");
        add.addActionListener(e -> openDialog(null));
        JButton edit = UiUtils.button("Edit", "Edit the selected customer");
        edit.addActionListener(e -> {
            Customer c = getSelected();
            if (c != null) {
                openDialog(c);
            }
        });
        JButton delete = UiUtils.button("Delete", "Delete the selected customer (only without orders)");
        delete.addActionListener(e -> deleteSelected());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);
        add(buttons, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        model.clear();
        for (Customer c : frame.getShop().getCustomers()) {
            model.addRow(new Object[]{c.getId(), c.getFullName(), c.getEmail(), c.getPhone(),
                    c.getAddress(), frame.getShop().getOrdersOf(c).size()});
        }
    }

    private Customer getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtils.showInfo(this, "Please select a customer in the table first.");
            return null;
        }
        return frame.getShop().getCustomers().get(row);
    }

    private void openDialog(Customer customer) {
        CustomerDialog dialog = new CustomerDialog(frame, frame.getShop(), customer);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            frame.saveData();
            refresh();
        }
    }

    private void deleteSelected() {
        Customer c = getSelected();
        if (c == null || !UiUtils.confirm(this, "Delete customer \"" + c.getFullName() + "\"?")) {
            return;
        }
        try {
            frame.getShop().deleteCustomer(c);
            frame.saveData();
            frame.setStatus("Customer \"" + c.getFullName() + "\" deleted.");
            refresh();
        } catch (BusinessException e) {
            UiUtils.showError(this, e.getMessage());
        }
    }
}
