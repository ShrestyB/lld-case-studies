/** Money is in; the customer can buy or cancel. */
class HasMoneyState implements MachineState {
    @Override public void moneyInserted(VendingMachine machine) {
        // More coins: stay in this state.
    }

    @Override public int selectProduct(VendingMachine machine, String name) {
        int change = machine.sell(name);           // throws before any state change if the sale is invalid
        machine.setState(new IdleState());         // vending is instantaneous, so no dispensing state
        return change;
    }

    @Override public int cancel(VendingMachine machine) {
        int refund = machine.refund();
        machine.setState(new IdleState());
        return refund;
    }
}
