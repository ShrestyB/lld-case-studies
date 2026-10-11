/** Demo run. The only place concrete classes are created. */
public class Main {
    public static void main(String[] args) {
        CoinPayment coinSlot = new CoinPayment();
        VendingMachine machine = new VendingMachine(coinSlot);

        operatorFlow(machine);
        customerFlow(machine, coinSlot);
        System.out.println("Operator collects revenue: " + machine.collectRevenue());
    }

    static void operatorFlow(OperatorPanel operator) {
        operator.addProduct(new Product("chips", 500, 1));
        operator.addProduct(new Product("soda", 300, 2));
        operator.restock("chips", 2);
    }

    static void customerFlow(CustomerPanel panel, CoinPayment coinSlot) {
        // A normal sale with change
        coinSlot.insertCoin(Coin.TEN);
        panel.moneyInserted();
        System.out.println("Bought chips, change: " + panel.selectProduct("chips"));

        // Not enough money: rejected, balance kept, customer adds more and picks something else
        coinSlot.insertCoin(Coin.TWO);
        panel.moneyInserted();
        try {
            panel.selectProduct("chips");
        } catch (RuntimeException e) {
            System.out.println("Rejected: " + e.getMessage());
            coinSlot.insertCoin(Coin.ONE);
            panel.moneyInserted();
            System.out.println("Added Rs 1, bought soda, change: " + panel.selectProduct("soda"));
        }

        // Change of mind: cancel refunds everything inserted
        coinSlot.insertCoin(Coin.FIVE);
        panel.moneyInserted();
        System.out.println("Cancelled, refund: " + panel.cancel());
    }
}
