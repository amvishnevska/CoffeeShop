package coffeeshop.ui;

import coffeeshop.exception.BusinessException;
import coffeeshop.exception.ValidationException;
import coffeeshop.model.Category;
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
 * Dialog for adding or editing a category.
 */
public class CategoryDialog extends JDialog {

    private final Shop shop;
    private final Category category;
    private final JTextField nameField = new JTextField(25);
    private final JTextField descriptionField = new JTextField(25);
    private boolean saved = false;

    public CategoryDialog(JFrame owner, Shop shop, Category category) {
        super(owner, category == null ? "Add category" : "Edit category", true);
        this.shop = shop;
        this.category = category;

        FormPanel form = new FormPanel();
        form.addRow("Name *", nameField, "Unique category name, e.g. Coffee beans");
        form.addRow("Description", descriptionField, "Short description, up to 200 characters");
        if (category != null) {
            nameField.setText(category.getName());
            descriptionField.setText(category.getDescription());
        }

        JButton save = UiUtils.button("Save", "Check the data and save the category");
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
            String name = Validator.requireText(nameField.getText(), "Name", 50);
            String description = descriptionField.getText().trim();
            if (description.length() > 200) {
                throw new ValidationException("Description must be at most 200 characters.");
            }
            if (category == null) {
                shop.addCategory(name, description);
            } else {
                shop.updateCategory(category, name, description);
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
