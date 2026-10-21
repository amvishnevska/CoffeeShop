package coffeeshop.model;

public interface Discountable {

   int MAX_DISCOUNT = 70;

  int getDiscountPercent();

  void setDiscountPercent(int percent);

   double getDiscountedPrice();
}
