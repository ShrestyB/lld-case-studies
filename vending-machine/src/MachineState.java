/** What each customer operation means in the machine's current state. */
interface MachineState {
    void moneyInserted(VendingMachine machine);
    int selectProduct(VendingMachine machine, String name);
    int cancel(VendingMachine machine);
}
