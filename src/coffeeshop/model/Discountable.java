package coffeeshop.model;

/** Contract for products that can be sold with a discount. */
public interface Discountable {

    /** Maximum allowed discount in percent. */
   int MAX_DISCOUNT = 70;

  int getDiscountPercent();

    /** Sets discount in percent. */
  void setDiscountPercent(int percent);

   double getDiscountedPrice();
}
