/** No money inserted yet. */
class IdleState implements MachineState {
    @Override public void moneyInserted(VendingMachine machine) {
        machine.setState(new HasMoneyState());
    }

    @Override public int selectProduct(VendingMachine machine, String name) {
        throw new IllegalStateException("Insert money first");
    }

    @Override public int cancel(VendingMachine machine) {
        return machine.refund();
    }
}
