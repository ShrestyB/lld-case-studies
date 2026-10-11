/** What a customer can do. Operator actions are not reachable through this type. */
public interface CustomerPanel {
    void moneyInserted();
    int selectProduct(String name);   // returns change, in paise
    int cancel();                     // returns the refund, in paise
}
