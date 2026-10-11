/** A product guards its own invariants: a name, a non-negative price, stock that never goes below zero. */
public class Product {
    private final String name;
    private final int price;          // paise
    private int quantity;

    public Product(String name, int price, int quantity) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (price < 0)                      throw new IllegalArgumentException("Price cannot be negative");
        if (quantity < 0)                   throw new IllegalArgumentException("Quantity cannot be negative");
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName()  { return name; }
    public int getPrice()    { return price; }
    public int getQuantity() { return quantity; }

    public void reduceStock() {
        if (quantity == 0) throw new IllegalStateException("Out of stock: " + name);
        quantity--;
    }

    public void addStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Restock amount must be positive");
        quantity += amount;
    }
}
