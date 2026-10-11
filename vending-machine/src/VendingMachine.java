import java.util.HashMap;
import java.util.Map;

/**
 * Coordinates the catalogue, the current state and the revenue taken.
 * It never knows how money arrives — that is the injected PaymentMethod.
 */
public class VendingMachine implements CustomerPanel, OperatorPanel {
    private final PaymentMethod paymentMethod;
    private final Map<String, Product> catalog = new HashMap<>();
    private MachineState state = new IdleState();
    private int revenue = 0;                       // paise taken since the last collectRevenue(), from any payment method

    public VendingMachine(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // ---- customer ----------------------------------------------------------
    @Override public void moneyInserted()           { state.moneyInserted(this); }
    @Override public int selectProduct(String name) { return state.selectProduct(this, name); }
    @Override public int cancel()                   { return state.cancel(this); }

    // ---- operator ----------------------------------------------------------
    @Override public void addProduct(Product product) {
        catalog.put(product.getName(), product);
    }

    @Override public void restock(String name, int quantity) {
        findProduct(name).addStock(quantity);
    }

    @Override public int collectRevenue() {
        int total = revenue;
        revenue = 0;
        return total;
    }

    // ---- used by the states --------------------------------------------------
    void setState(MachineState next) { state = next; }

    int refund() { return paymentMethod.refund(); }

    /** Validates everything first, so a rejected sale changes nothing. */
    int sell(String name) {
        Product product = findProduct(name);
        if (product.getQuantity() == 0)             throw new IllegalStateException("Out of stock: " + name);
        if (!paymentMethod.canPay(product.getPrice())) throw new IllegalStateException("Insufficient funds");
        product.reduceStock();
        revenue += product.getPrice();
        return paymentMethod.pay(product.getPrice());
    }

    private Product findProduct(String name) {
        Product product = catalog.get(name);
        if (product == null) throw new IllegalArgumentException("Unknown product: " + name);
        return product;
    }
}
