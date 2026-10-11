/**
 * Card payment. Added as the change test (README §6) — it required no edits to VendingMachine.
 * Always approves; a real implementation would call a payment gateway.
 */
public class CardPayment implements PaymentMethod {
    @Override public boolean canPay(int priceInPaise) { return true; }
    @Override public int pay(int priceInPaise)        { return 0; }
    @Override public int refund()                     { return 0; }
}
