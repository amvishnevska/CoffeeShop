package coffeeshop.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests of the stock check Product.isAvailable(quantity) (requirements FR8, FR9). */
class ProductAvailabilityTest {

    @ParameterizedTest(name = "stock {0}, quantity {1} -> {2}")
    @CsvSource({
            "4, 1, true",    // usual order
            "4, 4, true",    // the last items in stock (border)
            "4, 5, false",   // one item more than in stock (border)
            "4, 0, false",   // zero quantity is not an order (border)
            "0, 1, false"    // product is sold out
    })
    void isAvailable(int stock, int quantity, boolean expected) {
        Coffee coffee = new Coffee("Italian Espresso", "", 8.90, stock, new Category("Ground coffee", ""),
                "Lavazza", RoastLevel.DARK, "Blend", 250, true);

        assertEquals(expected, coffee.isAvailable(quantity));
    }
}
