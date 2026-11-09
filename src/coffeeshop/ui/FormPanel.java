package coffeeshop.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panel that arranges input fields in two columns: "label | field".
 * Every field gets a tooltip (hint) explaining what should be entered.
 */
public class FormPanel extends JPanel {

    private int row = 0;

    public FormPanel() {
        super(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    /** Adds a row with a label and an input field. */
    public void addRow(String label, JComponent field, String hint) {
        JLabel jLabel = new JLabel(label);
        jLabel.setLabelFor(field);
        field.setToolTipText(hint);
        jLabel.setToolTipText(hint);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(4, 4, 4, 8);
        add(jLabel, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 0, 4, 4);
        add(field, c);
        row++;
    }

    /** Adds a row with a label and an input field without a hint. */
    public void addRow(String label, JComponent field) {
        addRow(label, field, null);
    }

    /** Adds a component that takes the whole row (e.g. a section title). */
    public void addFullRow(JComponent component) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 4, 4, 4);
        add(component, c);
    }
}
