# Jogo da Memória

A memory card game built with Java and JavaFX. Flip cards to find matching pairs before running out of attempts. Features two game modes and a bonus card mechanic.

---

## Requirements

- Java 17 or higher (has to be liberica full JDK or download javaFX separately).
---

## How to Run

```bash
git clone <repository-url>
cd JogoMemoria
```

Or extract the ZIP and open a terminal inside the `JogoMemoria` folder.

If anything related to javafx doesn't work when you try to run the game, it's probably because your IDE is selecting the wrong SDK or you simply downloaded the wrong version.

## How to Play

1. Launch the game thorugh the App.java — a menu will appear.
2. **Choose a game mode:**
   - **Números** — cards display numbers (1–10).
   - **Cartas** — cards display playing card images from the resources folder.
3. Click **Start** to begin.
4. You have a **4×5 grid** of face-down cards (20 cards total = 10 pairs).
5. Click a card to flip it, then click a second card:
   - **Match** — both cards stay face up and are marked as found.
   - **No match** — both cards flip back face down after a short delay.
6. Each pair attempt costs **1 try**. You start with **20 attempts**.
7. **Bonus cards:** one pair on the board is a bonus pair. Finding it grants **+2 extra attempts**.
8. **Win** by finding all 10 pairs before running out of attempts.
9. **Lose** if attempts reach 0 with pairs still hidden.
10. After a win or loss, you are returned to the main menu to play again.

---
