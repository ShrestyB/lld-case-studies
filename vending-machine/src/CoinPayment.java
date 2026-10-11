/** Coins inserted into the machine's slot. */
public class CoinPayment implements PaymentMethod {
    private int balanceInPaise = 0;

    public void insertCoin(Coin coin) {
        balanceInPaise += coin.getPaise();
    }

    @Override public boolean canPay(int priceInPaise) {
        return balanceInPaise >= priceInPaise;
    }

    @Override public int pay(int priceInPaise) {
        int change = balanceInPaise - priceInPaise;
        balanceInPaise = 0;
        return change;
    }

    @Override public int refund() {
        int amount = balanceInPaise;
        balanceInPaise = 0;
        return amount;
    }
}
