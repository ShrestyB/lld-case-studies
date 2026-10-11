/** What an operator can do. */
public interface OperatorPanel {
    void addProduct(Product product);
    void restock(String name, int quantity);
    int collectRevenue();             // returns revenue taken since the last call, in paise
}
