package coffeeshop.ui;

import coffeeshop.exception.BusinessException;
import coffeeshop.exception.ValidationException;
import coffeeshop.model.Customer;
import coffeeshop.service.Shop;
import coffeeshop.util.Validator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Dialog for adding or editing a customer.
 */
public class CustomerDialog extends JDialog {

    private final Shop shop;
    private final Customer customer;
    private final JTextField nameField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField phoneField = new JTextField(25);
    private final JTextField addressField = new JTextField(25);
    private boolean saved = false;

    public CustomerDialog(JFrame owner, Shop shop, Customer customer) {
        super(owner, customer == null ? "Add customer" : "Edit customer", true);
        this.shop = shop;
        this.customer = customer;

        FormPanel form = new FormPanel();
        form.addRow("Full name *", nameField, "Name and surname, e.g. Anna Ozola");
        form.addRow("E-mail *", emailField, "Unique e-mail, e.g. anna@mail.com");
        form.addRow("Phone", phoneField, "Optional, 7-15 digits, e.g. +371 20000000");
        form.addRow("Address *", addressField, "Delivery address");
        if (customer != null) {
            nameField.setText(customer.getFullName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());
            addressField.setText(customer.getAddress());
        }

        JButton save = UiUtils.button("Save", "Check the data and save the customer");
        save.addActionListener(e -> onSave());
        JButton cancel = UiUtils.button("Cancel", "Close without saving");
        cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(save);
        buttons.add(cancel);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);
        pack();
        setLocationRelativeTo(owner);
    }

    private void onSave() {
        try {
            String name = Validator.requireText(nameField.getText(), "Full name");
            String email = Validator.requireEmail(emailField.getText());
            String phone = Validator.checkPhone(phoneField.getText());
            String address = Validator.requireText(addressField.getText(), "Address", 200);

            if (customer == null) {
                shop.addCustomer(new Customer(name, email, phone, address));
            } else {
                shop.checkEmailUnique(email, customer);
                customer.setFullName(name);
                customer.setEmail(email);
                customer.setPhone(phone);
                customer.setAddress(address);
            }
            saved = true;
            dispose();
        } catch (ValidationException | BusinessException e) {
            UiUtils.showError(this, e.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
