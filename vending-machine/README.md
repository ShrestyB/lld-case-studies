# Vending machine

A vending machine in Java, built from requirements rather than from classes. The
interesting part is not that it dispenses snacks — it is that **three separate forces
push on the design**, and each one is absorbed by a different boundary:

| Force | Absorbed by |
|---|---|
| Behaviour depends on *when* you are (no money / money in) | **State pattern** |
| Two audiences with different rights (customer vs operator) | **Two role interfaces** |
| Money can arrive in more than one way (coins, card, …) | **An injected `PaymentMethod`** |

---

## 1. Requirements

**Functional**
- Accepts ₹1, ₹2, ₹5 and ₹10 coins. **All money is integer paise** — never floating point.
- Customer selects a product by name.
- Dispenses if the balance covers the price **and** stock remains; returns the change.
- Cancel refunds the full inserted balance.
- Operator can add products, restock, and collect the revenue taken since the last collection.

**Non-functional** — one machine, one customer at a time, in memory only.

**Out of scope, deliberately** — networked fleet · real hardware · receipts · concurrency ·
persistence · running out of *change* · carts · categories.
*Card payment is deliberately out of scope as a feature, and used as the change test in §6.*

---

## 2. Entities, and why each boundary exists

| Class | Responsibility | Why it is separate |
|---|---|---|
| `Coin` | The denominations that exist, in paise | An enum makes an invalid coin **unrepresentable**. No validation code is needed because a bad coin cannot be constructed |
| `Product` | Its own name, price and stock, and guards them | It rejects a blank name, a negative price and an over-sell in its own constructor and `reduceStock()`. Nobody else has to re-check |
| `VendingMachine` | Owns the catalogue, the revenue taken, and the current state | It coordinates; it does not compute money and does not decide state-specific behaviour |
| `MachineState` + 2 states | What each operation *means right now* | Without this, every method needs an if-chain over the current state, and adding a state means editing all three |
| `PaymentMethod` | How money is taken and refunded | Lets the machine work with coins, cards or a test double without knowing which |
| `CustomerPanel` / `OperatorPanel` | Two different audiences | A customer-facing screen holding a `CustomerPanel` **cannot compile** a call to `collectRevenue()` |

**Field vs local** — `state`, `revenue`, `catalog` and `paymentMethod` are fields because
they must survive between calls. Everything else (a product name, a price being compared) is
a parameter or a local: data for one call, not state of the object.

---

## 3. Key behaviour

```
insertCoin(coin)          → CoinPayment accumulates paise   (the machine never sees coins)
moneyInserted()           → Idle ──► HasMoney
selectProduct(name)       → HasMoney: look up · check stock · ask PaymentMethod.canPay
                            → charge, reduce stock, return change, back to Idle
                          → Idle: rejected, "Insert money first"
cancel()                  → refund whatever the PaymentMethod holds, back to Idle
```

Every failure — unknown product, out of stock, insufficient funds — **throws before any
state changes**, so a rejected sale leaves the money exactly where it was. That property is
what tests 1, 2 and 3 actually check.

---

## 4. Patterns used — and why

| Pattern | Where | The pain it removed |
|---|---|---|
| **State** | `MachineState` · `IdleState` · `HasMoneyState` | Before it, `selectProduct` and `cancel` each needed an if-chain over the current mode, and a new mode meant editing every method. Now a state is one class, and "you can't select before paying" is enforced by *which object is installed*, not by a flag |
| **Strategy (via DIP)** | `PaymentMethod` ← `CoinPayment`, `CardPayment`, `FakePayment` | The machine no longer knows *how* money arrives. Adding card payment touched zero lines of `VendingMachine` |
| **Role interfaces (ISP)** | `CustomerPanel` · `OperatorPanel` | A fat interface would have exposed `collectRevenue()` and `restock()` to the customer-facing code. Splitting them makes misuse a **compile error** rather than a code-review comment |

**Considered and rejected — Singleton for `VendingMachine`.** It is the classic answer for
"there is only one machine", and it is wrong here: it makes the machine a global, and every
test would then share one instance. Each test below constructs its own machine, which is only
possible because the class is ordinary.

---

## 5. Trade-offs I accepted

- **Money as integer paise.** No floating-point error, at the cost of converting at the edges.
- **In-memory only.** Enough to show the design; no persistence layer.
- **Single-threaded.** Two customers at once would race on `state` and `revenue` — see §7.
- **Exceptions for business rejections.** "Out of stock" throws rather than returning a result
  type. Simple and readable here; a real system would prefer a result object over control flow.
- **One revenue total, not a cash-vs-card split.** `collectRevenue()` counts every sale whatever the payment
  method. A real machine would report physical cash in the coin box separately from card revenue settled by
  the bank; for one in-memory machine a single total is enough.
- **No dispensing state.** Vending is treated as instantaneous, so a sale goes straight from *has money* back
  to *idle*. A physical release delay would add a third state that rejects every operation until the product
  drops — one new class, plus one line in `HasMoneyState` to enter it.
- **`VendingMachine` is not a Singleton** — see §4. Testability was worth more than enforced uniqueness.

---

## 6. Change test — add card payments

**Classes added:** 1 (`CardPayment implements PaymentMethod`).
**Existing classes edited: 0.** `VendingMachine` compiles untouched, because it depends on the
interface and the concrete type is chosen in `Main` — the only place `new CoinPayment()` appears.

That zero is the whole point of §4's second row, and it is the test that proves the boundary
was drawn in the right place.

---

## 7. What changes at scale

**Concurrency:** `state` and `revenue` need synchronisation or an actor-per-machine model;
the catalogue moves out of memory; `sell()` becomes a transaction, because reducing stock and
taking payment must not be able to half-succeed.

---

## Running it

Plain Java, no build tool, no dependencies. From this folder:

```
javac -d out src/*.java test/*.java
java -cp out VendingMachineTest     # 27 checks across 7 tests
java -cp out Main                   # a short demo run
```

## Layout

```
vending-machine/
├── src/     Coin · Product · PaymentMethod (+ CoinPayment, CardPayment)
│            MachineState (+ Idle, HasMoney) · VendingMachine · the two panels · Main
└── test/    VendingMachineTest · FakePayment (a test double)
```

**On the tests:** cases 1 and 2 are mine; cases 3–7 were completed with AI assistance from my test plan.
