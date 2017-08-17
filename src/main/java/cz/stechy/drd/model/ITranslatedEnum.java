package cz.stechy.drd.model;

public interface ITranslatedEnum {

    /**
     * Vrátí klíč, který koresponduje s konstantou v překladatelském souboru
     *
     * @return Klíč pro překlad
     */
    String getKeyForTranslation();

}
