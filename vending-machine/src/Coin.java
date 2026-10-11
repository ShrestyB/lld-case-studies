/** Accepted coins. Values are held in paise, so money is always an integer. */
public enum Coin {
    ONE(100), TWO(200), FIVE(500), TEN(1000);

    private final int paise;

    Coin(int paise) { this.paise = paise; }

    public int getPaise() { return paise; }
}
