package cz.stechy.drd.model;

/**
 * Rozhraní pro objekty, které se dají klonovat
 */
public interface IClonable {

    /**
     * Vytvoří hlubokou kopii objektu
     *
     * @param <T> Typ objektu, který má být výsledkem kopie
     * @return Hlubokou kopii objektu
     */
    <T extends IClonable> T duplicate();

}
