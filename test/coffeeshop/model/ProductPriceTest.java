package coffeeshop.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests of the selling price with a discount (requirement FR6). */
class ProductPriceTest {

    private final Category machines = new Category("Coffee machines", "");

    private Equipment createMachine(double price) {
        return new Equipment("Espresso machine EC685", "", price, 4, machines, "DeLonghi", 24, "Steel", 1300);
    }

    @ParameterizedTest(name = "price {0}, discount {1}% -> {2}")
    @CsvSource({
            "249.00, 0,  249.00",   // no discount: selling price = price
            "249.00, 1,  246.51",   // smallest discount (border)
            "249.00, 15, 211.65",   // usual discount (demo data)
            "249.00, 70, 74.70",    // maximal discount (border)
            "0.01,   70, 0.003"     // smallest price with maximal discount
    })
    void sellingPriceWithDiscount(double price, int discount, double expected) {
        Equipment machine = createMachine(price);
        machine.setDiscountPercent(discount);

        assertEquals(expected, machine.getSellingPrice(), 0.001);
    }

    @ParameterizedTest(name = "discount {0}% is rejected")
    @ValueSource(ints = {-1, 71, 100})
    void discountOutsideRangeIsRejected(int discount) {
        Equipment machine = createMachine(249.00);

        assertThrows(IllegalArgumentException.class, () -> machine.setDiscountPercent(discount));
        assertEquals(0, machine.getDiscountPercent(), "discount must not change");
    }

    @Test
    void teaHasNoDiscount() {
        Tea tea = new Tea("Earl Grey", "", 5.20, 30, new Category("Tea", ""), "Twinings", TeaType.BLACK, 100, true);

        assertEquals(5.20, tea.getSellingPrice(), 0.001);
    }
}
