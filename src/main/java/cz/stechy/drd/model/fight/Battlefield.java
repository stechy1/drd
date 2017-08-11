package cz.stechy.drd.model.fight;

import cz.stechy.drd.model.Dice;
import cz.stechy.drd.model.entity.IAggressive;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Třída reprezentující bojiště pro souboje
 */
public final class Battlefield {

    // region Constants

    private static final Duration DELAY = Duration.millis(750);

    // endregion

    // region Variables

    final Timeline timeline = new Timeline();


    // endregion

    // region Constructors

    /**
     * Vytvoří nové bojiště pro dvě entity
     * @param aggressiveEntity1 První bojovník
     * @param aggressiveEntity2 Druhý bojovník
     */
    public Battlefield(IAggressive aggressiveEntity1, IAggressive aggressiveEntity2) {
        timeline.getKeyFrames().setAll(
            new KeyFrame(Duration.ZERO, event -> { // Kontrola že jsou všichni živí a zdraví
                if (!aggressiveEntity1.isAlive() || !aggressiveEntity2.isAlive()) {
                    timeline.stop();
                }
            }),
            new KeyFrame(DELAY, event -> { // Útočí první entita
                System.out.println("Útočí první entita");
                attack(aggressiveEntity1, aggressiveEntity2);
            }), // Utočí první
            new KeyFrame(Duration.ZERO, event -> { // Kontrola, že druhá entita je stále na živu
                if (!aggressiveEntity2.isAlive()) {
                    timeline.stop();
                }
            }),
            new KeyFrame(DELAY, event -> { // Útočí druhá entita
                System.out.println("Útočí druhá entita");
                attack(aggressiveEntity2, aggressiveEntity1);
            })  // Útočí druhý
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // endregion

    // region Private methods

    private void attack(IAggressive attacker, IAggressive defender) {
        final int attackNumber = Math.max(Dice.K6.roll() + attacker.getAttackNumber(), 0);
        final int defnenceNumber = Math.max(Dice.K6.roll() + defender.getDefenceNumber(), 0);
        final boolean attackSuccess = attackNumber > defnenceNumber;

        if (attackSuccess) { // Pokud byl útok úspěšný
            defender.subtractLive(Math.max(attackNumber - defnenceNumber, 1));
        }

        System.out.println("ÚČ: " + attackNumber + "; DEF: " + defnenceNumber + " delta: " + Math.max(attackNumber - defnenceNumber, 1));

    }

    // endregion

    // region Public methods

    /**
     * Spustí souboj
     */
    public void fight() {
        timeline.play();
    }

    /**
     * Zastaví souboj
     */
    public void stopFight() {
        timeline.stop();
    }

    // endregion
}
