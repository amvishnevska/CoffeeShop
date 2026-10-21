package coffeeshop.ui;

import coffeeshop.exception.BusinessException;
import coffeeshop.model.Category;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Page for managing product categories.
 */
public class CategoriesPanel extends JPanel implements Refreshable {

    private final MainFrame frame;
    private final ReadOnlyTableModel model = new ReadOnlyTableModel("ID", "Name", "Description", "Products");
    private final JTable table = UiUtils.table(model);

    public CategoriesPanel(MainFrame frame) {
        super(new BorderLayout());
        this.frame = frame;

        add(UiUtils.title("Categories"), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = UiUtils.button("Add category", "Create a new category");
        add.addActionListener(e -> openDialog(null));
        JButton edit = UiUtils.button("Edit", "Edit the selected category");
        edit.addActionListener(e -> {
            Category c = getSelected();
            if (c != null) {
                openDialog(c);
            }
        });
        JButton delete = UiUtils.button("Delete", "Delete the selected category (only if it has no products)");
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
        for (Category c : frame.getShop().getCategories()) {
            int count = frame.getShop().searchProducts(c).size();
            model.addRow(new Object[]{c.getId(), c.getName(), c.getDescription(), count});
        }
    }

    private Category getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtils.showInfo(this, "Please select a category in the table first.");
            return null;
        }
        List<Category> all = frame.getShop().getCategories();
        return all.get(row);
    }

    private void openDialog(Category category) {
        CategoryDialog dialog = new CategoryDialog(frame, frame.getShop(), category);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            frame.saveData();
            refresh();
        }
    }

    private void deleteSelected() {
        Category c = getSelected();
        if (c == null || !UiUtils.confirm(this, "Delete category \"" + c.getName() + "\"?")) {
            return;
        }
        try {
            frame.getShop().deleteCategory(c);
            frame.saveData();
            frame.setStatus("Category \"" + c.getName() + "\" deleted.");
            refresh();
        } catch (BusinessException e) {
            UiUtils.showError(this, e.getMessage());
        }
    }
}
