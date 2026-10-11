# Low-Level Design — case studies in Java

Object-oriented design problems worked from **requirements first, classes second**.
Each case study records *why* every boundary exists, which design patterns earned their
place (and which were rejected), and whether the design survives a change in requirements.

> **Status:** 1 case study complete · 3 planned. Each is added only once it has working
> code, passing tests and a full write-up.

---

## Case studies

| # | Problem | Status | Core design question | Patterns that earned their place | Change test |
|---|---|---|---|---|---|
| 1 | [Vending machine](./vending-machine) | ✅ Complete | Behaviour depends on *when* you are, *who* is asking, and *how* money arrives | State · Strategy (via DIP) · role interfaces (ISP) | Add card payments → **0 existing classes edited** |
| 2 | Parking lot | 🔄 In progress | Allocation and pricing vary independently of the lot | Strategy · Factory | Add EV charging spots |
| 3 | LRU cache | ⏳ Planned | O(1) `get`/`put` needs a hashmap *and* an ordering | Strategy (eviction policy) | Switch the policy to LFU |
| 4 | Rate limiter | ⏳ Planned | The limiting algorithm must be swappable per client | Strategy · Decorator | Add a per-user tier |

---

## How to read a case study

Every folder has a README with the same seven sections, so they can be compared side by side:

1. **Requirements** — functional, non-functional, and what is *deliberately* out of scope
2. **Entities, and why each boundary exists** — including which data is a field and which is a local
3. **Key behaviour** — the two or three flows that matter
4. **Patterns used — and the pain each one removed** — plus the ones considered and rejected
5. **Trade-offs accepted**
6. **Change test** — one new requirement, and how many existing classes it forces you to edit
7. **Known issues and what changes at scale**

**Section 6 is the one to read first.** A design is only as good as its answer to "what
happens when the requirements move?" — and that answer is a number, not an opinion.

---

## Patterns index

The point is *when* a pattern is justified, not a catalogue.

| Pattern | Used in | The pain it removed |
|---|---|---|
| **State** | vending machine | Every operation needed an if-chain over the current mode; a new mode meant editing every method |
| **Strategy (via Dependency Inversion)** | vending machine | The machine knew *how* money arrived; adding a payment type meant editing the machine |
| **Interface segregation** | vending machine | One interface exposed operator actions (`collectRevenue`, `restock`) to customer-facing code |

**Considered and rejected**

| Pattern | Where | Why not |
|---|---|---|
| **Singleton** | vending machine | "Only one machine" is a deployment fact, not a design constraint. Making it global would force every test to share one instance |

---

## Principles these designs follow

- **Requirements before classes.** Out-of-scope items are written down, not just left out.
- **A field must survive between calls; anything else is a parameter or a local.**
- **Invalid states should be unrepresentable** — an enum for coins beats validating integers.
- **Depend on interfaces, create concrete objects at the edge** (`main`), and nowhere else.
- **No framework.** Dependency injection is done by hand so the wiring stays visible.
- **Known defects are documented, not hidden.** Each case study lists what is still wrong.

---

## Running the code

Plain Java, no build tool, no dependencies. Tested with JDK 24.

```
cd vending-machine
javac -d out src/*.java test/*.java
java -cp out VendingMachineTest     # the test suite
java -cp out Main                   # a short demo run
```

---

## Repository layout

```
lld-case-studies/
├── README.md              ← you are here
├── LICENSE
└── vending-machine/
    ├── README.md          ← the case study
    ├── src/               ← implementation, one class per file
    └── test/              ← tests and a test double
```
