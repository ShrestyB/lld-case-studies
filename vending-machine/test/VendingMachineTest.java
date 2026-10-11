/** Plain-Java test runner — no framework. Each test builds its own machine. */
public class VendingMachineTest {
    private static int passed = 0, failed = 0;

    public static void main(String[] args) {
        insufficientBalanceKeepsMoney();
        outOfStockKeepsMoney();
        unknownProductKeepsMoney();
        cancelRefundsAndReturnsToIdle();
        exactPaymentAndOverpayment();
        declinedPaymentNeverCharges();
        approvedPaymentChargesThePrice();

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + name);
        if (ok) passed++; else failed++;
    }

    private static boolean throwsOnSelect(VendingMachine m, String product) {
        try { m.selectProduct(product); return false; }
        catch (RuntimeException e) { return true; }
    }

    static void insufficientBalanceKeepsMoney() {
        CoinPayment coins = new CoinPayment();
        VendingMachine m = new VendingMachine(coins);
        m.addProduct(new Product("chips", 500, 3));

        coins.insertCoin(Coin.TWO);
        m.moneyInserted();

        check("insufficient: rejected", throwsOnSelect(m, "chips"));
        check("insufficient: balance kept", m.cancel() == 200);
        check("insufficient: no revenue taken", m.collectRevenue() == 0);
    }

    static void outOfStockKeepsMoney() {
        CoinPayment coins = new CoinPayment();
        VendingMachine m = new VendingMachine(coins);
        m.addProduct(new Product("chocolate", 200, 0));

        coins.insertCoin(Coin.TEN);
        m.moneyInserted();

        check("out of stock: rejected", throwsOnSelect(m, "chocolate"));
        check("out of stock: balance kept", m.cancel() == 1000);
        check("out of stock: no revenue taken", m.collectRevenue() == 0);
    }

    static void unknownProductKeepsMoney() {
        CoinPayment coins = new CoinPayment();
        VendingMachine m = new VendingMachine(coins);
        m.addProduct(new Product("chips", 500, 3));

        coins.insertCoin(Coin.TEN);
        m.moneyInserted();

        check("unknown product: rejected", throwsOnSelect(m, "soda"));
        check("unknown product: balance kept", m.cancel() == 1000);
        check("unknown product: no revenue taken", m.collectRevenue() == 0);
    }

    static void cancelRefundsAndReturnsToIdle() {
        CoinPayment coins = new CoinPayment();
        VendingMachine m = new VendingMachine(coins);
        m.addProduct(new Product("chips", 500, 3));

        coins.insertCoin(Coin.FIVE);
        m.moneyInserted();
        int refund = m.cancel();

        check("cancel: full refund", refund == 500);
        check("cancel: back to idle, so select is rejected", throwsOnSelect(m, "chips"));
        check("cancel: a second cancel refunds nothing", m.cancel() == 0);
        check("cancel: no revenue taken", m.collectRevenue() == 0);
    }

    static void exactPaymentAndOverpayment() {
        CoinPayment coins = new CoinPayment();
        VendingMachine m = new VendingMachine(coins);
        Product chips = new Product("chips", 1000, 2);
        m.addProduct(chips);

        coins.insertCoin(Coin.TEN);
        m.moneyInserted();
        int exactChange = m.selectProduct("chips");

        check("exact: no change", exactChange == 0);
        check("exact: stock 2 -> 1", chips.getQuantity() == 1);
        check("exact: revenue collected", m.collectRevenue() == 1000);

        coins.insertCoin(Coin.TEN);
        m.moneyInserted();
        coins.insertCoin(Coin.FIVE);
        m.moneyInserted();
        int overpayChange = m.selectProduct("chips");

        check("overpay: change returned", overpayChange == 500);
        check("overpay: stock 1 -> 0", chips.getQuantity() == 0);
        check("overpay: revenue collected", m.collectRevenue() == 1000);
    }

    static void declinedPaymentNeverCharges() {
        FakePayment fake = new FakePayment(false);
        VendingMachine m = new VendingMachine(fake);
        Product chips = new Product("chips", 500, 3);
        m.addProduct(chips);

        m.moneyInserted();

        check("declined: rejected", throwsOnSelect(m, "chips"));
        check("declined: pay() never called", fake.paidAmount == -1);
        check("declined: stock unchanged", chips.getQuantity() == 3);
        check("declined: no revenue taken", m.collectRevenue() == 0);
    }

    static void approvedPaymentChargesThePrice() {
        FakePayment fake = new FakePayment(true);
        VendingMachine m = new VendingMachine(fake);
        Product chips = new Product("chips", 500, 3);
        m.addProduct(chips);

        m.moneyInserted();
        int change = m.selectProduct("chips");

        check("approved: no change", change == 0);
        check("approved: charged exactly the price", fake.paidAmount == 500);
        check("approved: stock 3 -> 2", chips.getQuantity() == 2);
        // A card sale is revenue too — collectRevenue() covers every payment method (README §5).
        check("approved: counted in revenue", m.collectRevenue() == 500);
    }
}
