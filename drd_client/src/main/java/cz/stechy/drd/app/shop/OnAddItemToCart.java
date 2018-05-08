package cz.stechy.drd.app.shop;

/**
 * Rozhraní pro přístup k metodě, která se zavolá, když se přidá položka do košíku.
 *
 * @param <S> {@link cz.stechy.drd.app.shop.entry.ShopEntry}
 */
@FunctionalInterface
public interface OnAddItemToCart<S> {

    /**
     * Metoda se zavolá vždy, když se do košíku vloží nová nákupní položky
     *
     * @param entry {@link S}
     */
    void onAdd(S entry);
}
