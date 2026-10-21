package coffeeshop.ui;

import coffeeshop.exception.ValidationException;
import coffeeshop.model.Category;
import coffeeshop.model.Coffee;
import coffeeshop.model.Discountable;
import coffeeshop.model.Equipment;
import coffeeshop.model.Product;
import coffeeshop.model.ProductType;
import coffeeshop.model.RoastLevel;
import coffeeshop.model.Tea;
import coffeeshop.model.TeaType;
import coffeeshop.service.Shop;
import coffeeshop.util.Validator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

/**
 * Dialog for adding a new product or editing an existing one.
 * Common fields are always shown; the lower part changes depending on the product type.
 */
public class ProductDialog extends JDialog {

    private final Shop shop;
    private Product product;
    private boolean saved = false;

    private final JComboBox<ProductType> typeBox = new JComboBox<>(ProductType.values());
    private final JTextField nameField = new JTextField(25);
    private final JTextField brandField = new JTextField(25);
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JTextField priceField = new JTextField(10);
    private final JTextField stockField = new JTextField(10);
    private final JTextField ratingField = new JTextField("0", 10);
    private final JTextArea descriptionArea = new JTextArea(3, 25);

    private final JComboBox<RoastLevel> roastBox = new JComboBox<>(RoastLevel.values());
    private final JTextField originField = new JTextField(15);
    private final JTextField coffeeWeightField = new JTextField("250", 10);
    private final JCheckBox groundCheck = new JCheckBox("Ground coffee");
    private final JTextField coffeeDiscountField = new JTextField("0", 10);

    private final JComboBox<TeaType> teaTypeBox = new JComboBox<>(TeaType.values());
    private final JTextField teaWeightField = new JTextField("100", 10);
    private final JCheckBox baggedCheck = new JCheckBox("Tea bags");

    private final JTextField warrantyField = new JTextField("12", 10);
    private final JTextField materialField = new JTextField(15);
    private final JTextField powerField = new JTextField("0", 10);
    private final JTextField equipmentDiscountField = new JTextField("0", 10);

    private final CardLayout specificLayout = new CardLayout();
    private final JPanel specificPanel = new JPanel(specificLayout);

    public ProductDialog(JFrame owner, Shop shop, Product product) {
        super(owner, product == null ? "Add product" : "Edit product", true);
        this.shop = shop;
        this.product = product;

        for (Category c : shop.getCategories()) {
            categoryBox.addItem(c);
        }

        FormPanel common = new FormPanel();
        common.addRow("Product type *", typeBox, "Type cannot be changed after the product is created");
        common.addRow("Name *", nameField, "Product name, up to 100 characters");
        common.addRow("Brand", brandField, "Manufacturer, e.g. Lavazza");
        common.addRow("Category *", categoryBox, "Category in the catalog");
        common.addRow("Price, EUR *", priceField, "Price greater than 0, e.g. 12.50");
        common.addRow("In stock *", stockField, "Number of items in stock, 0 or more");
        common.addRow("Rating (0-5)", ratingField, "Customer rating from 0 to 5, e.g. 4.5");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        common.addRow("Description", new JScrollPane(descriptionArea), "Short description, up to 500 characters");

        specificPanel.add(createCoffeePanel(), ProductType.COFFEE.name());
        specificPanel.add(createTeaPanel(), ProductType.TEA.name());
        specificPanel.add(createEquipmentPanel(), ProductType.EQUIPMENT.name());
        specificPanel.setBorder(BorderFactory.createTitledBorder("Specific attributes"));
        typeBox.addActionListener(e -> specificLayout.show(specificPanel, getSelectedType().name()));

        JPanel center = new JPanel(new BorderLayout());
        center.add(common, BorderLayout.NORTH);
        center.add(specificPanel, BorderLayout.CENTER);

        JLabel hint = new JLabel("  Fields marked with * are required.");
        JButton save = UiUtils.button("Save", "Check the data and save the product");
        save.addActionListener(e -> onSave());
        JButton cancel = UiUtils.button("Cancel", "Close without saving");
        cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(save);
        buttons.add(cancel);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(hint, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);

        if (product != null) {
            fillFields(product);
            typeBox.setEnabled(false);
        }
        specificLayout.show(specificPanel, getSelectedType().name());

        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel createCoffeePanel() {
        FormPanel p = new FormPanel();
        p.addRow("Roast level *", roastBox, "How dark the beans are roasted");
        p.addRow("Origin", originField, "Country of origin or \"Blend\"");
        p.addRow("Weight, g *", coffeeWeightField, "Package weight in grams, 1-10000");
        p.addRow("", groundCheck, "Check if the coffee is ground, uncheck for beans");
        p.addRow("Discount, % ", coffeeDiscountField, "Discount 0-" + Discountable.MAX_DISCOUNT + "%");
        return p;
    }

    private JPanel createTeaPanel() {
        FormPanel p = new FormPanel();
        p.addRow("Tea type *", teaTypeBox, "Kind of tea");
        p.addRow("Weight, g *", teaWeightField, "Package weight in grams, 1-10000");
        p.addRow("", baggedCheck, "Check if the tea is in bags, uncheck for loose leaf");
        return p;
    }

    private JPanel createEquipmentPanel() {
        FormPanel p = new FormPanel();
        p.addRow("Warranty, months *", warrantyField, "Warranty period 0-120 months");
        p.addRow("Material", materialField, "Main material, e.g. Stainless steel");
        p.addRow("Power, W *", powerField, "Power consumption 0-5000 W, 0 for non-electric items");
        p.addRow("Discount, % ", equipmentDiscountField, "Discount 0-" + Discountable.MAX_DISCOUNT + "%");
        return p;
    }

    private ProductType getSelectedType() {
        return (ProductType) typeBox.getSelectedItem();
    }

    private void fillFields(Product p) {
        typeBox.setSelectedItem(p.getType());
        nameField.setText(p.getName());
        brandField.setText(p.getBrand());
        categoryBox.setSelectedItem(p.getCategory());
        priceField.setText(String.valueOf(p.getPrice()));
        stockField.setText(String.valueOf(p.getStockQuantity()));
        ratingField.setText(String.valueOf(p.getRating()));
        descriptionArea.setText(p.getDescription());

        if (p instanceof Coffee) {
            Coffee c = (Coffee) p;
            roastBox.setSelectedItem(c.getRoastLevel());
            originField.setText(c.getOrigin());
            coffeeWeightField.setText(String.valueOf(c.getWeightGrams()));
            groundCheck.setSelected(c.isGround());
            coffeeDiscountField.setText(String.valueOf(c.getDiscountPercent()));
        } else if (p instanceof Tea) {
            Tea t = (Tea) p;
            teaTypeBox.setSelectedItem(t.getTeaType());
            teaWeightField.setText(String.valueOf(t.getWeightGrams()));
            baggedCheck.setSelected(t.isBagged());
        } else if (p instanceof Equipment) {
            Equipment eq = (Equipment) p;
            warrantyField.setText(String.valueOf(eq.getWarrantyMonths()));
            materialField.setText(eq.getMaterial());
            powerField.setText(String.valueOf(eq.getPowerWatts()));
            equipmentDiscountField.setText(String.valueOf(eq.getDiscountPercent()));
        }
    }

    private void onSave() {
        try {
            String name = Validator.requireText(nameField.getText(), "Name");
            String brand = brandField.getText().trim();
            if (brand.length() > Validator.MAX_TEXT_LENGTH) {
                throw new ValidationException("Field \"Brand\" must be at most " + Validator.MAX_TEXT_LENGTH + " characters.");
            }
            Category category = Validator.requireSelected((Category) categoryBox.getSelectedItem(), "Category");
            double price = Validator.parsePositiveDouble(priceField.getText(), "Price");
            int stock = Validator.parseInt(stockField.getText(), "In stock", 0, 100000);
            double rating = Validator.parseDouble(ratingField.getText(), "Rating", 0, 5);
            String description = descriptionArea.getText().trim();
            if (description.length() > 500) {
                throw new ValidationException("Description must be at most 500 characters.");
            }

            if (product == null) {
                product = createProduct(name, description, price, stock, category, brand);
                product.setRating(rating);
                shop.addProduct(product);
            } else {
                updateSpecificFields(product);
                product.setName(name);
                product.setBrand(brand);
                product.setCategory(category);
                product.setPrice(price);
                product.setStockQuantity(stock);
                product.setRating(rating);
                product.setDescription(description);
            }
            saved = true;
            dispose();
        } catch (ValidationException | IllegalArgumentException e) {
            UiUtils.showError(this, e.getMessage());
        }
    }

    private Product createProduct(String name, String description, double price, int stock,
                                  Category category, String brand) throws ValidationException {
        switch (getSelectedType()) {
            case COFFEE: {
                Coffee c = new Coffee(name, description, price, stock, category, brand,
                        (RoastLevel) roastBox.getSelectedItem(), originField.getText(),
                        Validator.parseInt(coffeeWeightField.getText(), "Weight", 1, 10000),
                        groundCheck.isSelected());
                c.setDiscountPercent(Validator.parseInt(coffeeDiscountField.getText(), "Discount", 0, Discountable.MAX_DISCOUNT));
                return c;
            }
            case TEA:
                return new Tea(name, description, price, stock, category, brand,
                        (TeaType) teaTypeBox.getSelectedItem(),
                        Validator.parseInt(teaWeightField.getText(), "Weight", 1, 10000),
                        baggedCheck.isSelected());
            default: {
                Equipment eq = new Equipment(name, description, price, stock, category, brand,
                        Validator.parseInt(warrantyField.getText(), "Warranty", 0, 120),
                        materialField.getText(),
                        Validator.parseInt(powerField.getText(), "Power", 0, 5000));
                eq.setDiscountPercent(Validator.parseInt(equipmentDiscountField.getText(), "Discount", 0, Discountable.MAX_DISCOUNT));
                return eq;
            }
        }
    }

    private void updateSpecificFields(Product p) throws ValidationException {
        if (p instanceof Coffee) {
            Coffee c = (Coffee) p;
            int weight = Validator.parseInt(coffeeWeightField.getText(), "Weight", 1, 10000);
            int discount = Validator.parseInt(coffeeDiscountField.getText(), "Discount", 0, Discountable.MAX_DISCOUNT);
            c.setRoastLevel((RoastLevel) roastBox.getSelectedItem());
            c.setOrigin(originField.getText());
            c.setWeightGrams(weight);
            c.setGround(groundCheck.isSelected());
            c.setDiscountPercent(discount);
        } else if (p instanceof Tea) {
            Tea t = (Tea) p;
            int weight = Validator.parseInt(teaWeightField.getText(), "Weight", 1, 10000);
            t.setTeaType((TeaType) teaTypeBox.getSelectedItem());
            t.setWeightGrams(weight);
            t.setBagged(baggedCheck.isSelected());
        } else if (p instanceof Equipment) {
            Equipment eq = (Equipment) p;
            int warranty = Validator.parseInt(warrantyField.getText(), "Warranty", 0, 120);
            int power = Validator.parseInt(powerField.getText(), "Power", 0, 5000);
            int discount = Validator.parseInt(equipmentDiscountField.getText(), "Discount", 0, Discountable.MAX_DISCOUNT);
            eq.setWarrantyMonths(warranty);
            eq.setMaterial(materialField.getText());
            eq.setPowerWatts(power);
            eq.setDiscountPercent(discount);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Product getProduct() {
        return product;
    }
}
