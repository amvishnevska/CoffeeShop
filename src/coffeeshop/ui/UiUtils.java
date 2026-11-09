package coffeeshop.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.Font;

/**
 * Small helper methods used by all windows.
 */
public final class UiUtils {

    private UiUtils() {
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shows a Yes/No question. */
    public static boolean confirm(Component parent, String question) {
        return JOptionPane.showConfirmDialog(parent, question, "Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Formats a sum of money, e.g. "12.50 EUR". */
    public static String money(double value) {
        return String.format("%.2f EUR", value);
    }

    /** Creates a button with a tooltip. */
    public static JButton button(String text, String hint) {
        JButton b = new JButton(text);
        b.setToolTipText(hint);
        return b;
    }

    /** Creates a big bold title for a page. */
    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    /** Creates a table that allows selecting only one row. */
    public static JTable table(ReadOnlyTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }
}
