/** How money is taken. VendingMachine depends on this, never on a concrete payment type. */
public interface PaymentMethod {
    boolean canPay(int priceInPaise);
    int pay(int priceInPaise);        // takes the price, returns the change
    int refund();                     // returns everything held, and empties it
}
