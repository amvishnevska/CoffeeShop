package coffeeshop.ui;

import javax.swing.table.DefaultTableModel;

/**
 * Table model whose cells cannot be edited directly in the table.
 * Data is changed only through dialogs with input validation.
 */
public class ReadOnlyTableModel extends DefaultTableModel {

    public ReadOnlyTableModel(String... columns) {
        super(columns, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /** Removes all rows. */
    public void clear() {
        setRowCount(0);
    }
}
