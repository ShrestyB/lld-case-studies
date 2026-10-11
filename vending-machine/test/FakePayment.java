/** Test double: approves or declines everything, and records what it was charged. */
class FakePayment implements PaymentMethod {
    private final boolean approve;
    int paidAmount = -1;                          // -1 means pay() was never called

    FakePayment(boolean approve) { this.approve = approve; }

    @Override public boolean canPay(int priceInPaise) { return approve; }

    @Override public int pay(int priceInPaise) {
        paidAmount = priceInPaise;
        return 0;
    }

    @Override public int refund() { return 0; }
}
