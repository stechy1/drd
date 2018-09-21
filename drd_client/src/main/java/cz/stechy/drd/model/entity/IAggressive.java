package cz.stechy.drd.model.entity;

/**
 * Rozhraní popisující takové entity, které umí útočit a bránit se
 */
public interface IAggressive {

    /**
     * @return Útočné číslo entity
     */
    int getAttackNumber();

    /**
     * @return Obranné číslo entity
     */
    int getDefenceNumber();

    /**
     * Odebere entite vybraný počet životů
     *
     * @param live Ubíraný počet životů
     */
    void subtractLive(int live);

    /**
     * Metoda pro zjištění, zda-li je entita ještě na živu
     *
     * @return True, pokud je entita ještě na živu, jinak False
     */
    boolean isAlive();
}
