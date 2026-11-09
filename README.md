# ☕ Coffee Shop – Online Store Product Catalog

Desktop application for a small online coffee shop. It keeps a product catalog by categories,
a list of customers and orders, and controls the stock automatically.

Course project for the module **Object-Oriented Programming**.
<img width="579" height="299" alt="image" src="https://github.com/user-attachments/assets/84a47dae-29c4-424f-b6fa-0c3cd1b9d147" />
## Features

- **Product catalog by categories** – coffee, tea and coffee equipment
- **Search and filter** by name, brand, category and price range
- **Products, categories and customers** – add, edit and delete with input validation
- **Discounts** for coffee and equipment with automatic selling price
- **Orders** – shopping cart, stock check, order status (New → Paid → Shipped → Delivered / Cancelled)
- **Statistics** – revenue and a list of products with low stock
- **Automatic saving** of all data to a file
### Product catalog
Products of the selected category, search by name or brand and price filter.
<img width="653" height="322" alt="image" src="https://github.com/user-attachments/assets/c56e9355-3d88-4545-bb81-d8a717c2aa82" />
### New order
Choose a customer, add products to the cart and place the order. The stock decreases automatically.
<img width="659" height="352" alt="image" src="https://github.com/user-attachments/assets/0b4c1ddf-c5f5-4de3-b96a-4550b19db319" />
### Orders
Filter by status, view order details and change the status. A cancelled order returns its items to stock.
<img width="658" height="348" alt="image" src="https://github.com/user-attachments/assets/ff77cda7-9405-48fc-a368-b55a7fc98a87" />
## How to run

1. Clone the repository:
   ```
   git clone https://github.com/amvishnevska/CoffeeShop.git
   ```
2. Open the folder in **IntelliJ IDEA** (JDK 11 or newer, tested with JDK 21).
3. Open `src/coffeeshop/Main.java` and press **Run**.

On the first start the program creates demo data in `data/shop.dat`.
Delete this file to start again with the demo data.

## How to run the tests

1. Mark the folder `test` as **Test Sources Root** and add **JUnit 5** to the project
   (IntelliJ suggests it on `@Test`).
2. Right click on the folder `test` → **Run 'All Tests'**.

22 unit tests check the selling price with a discount, the stock check, placing an order
and changing the order status.
