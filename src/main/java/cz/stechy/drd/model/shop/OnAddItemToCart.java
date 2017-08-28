package cz.stechy.drd.model.shop;

/**
 * Rozhraní pro přístup k metodě, která se zavolá, když se přidá položka do košíku.
 *
 * @param <S> {@link cz.stechy.drd.model.shop.entry.ShopEntry}
 */
@FunctionalInterface
public interface OnAddItemToCart<S> {

    void onAdd(S entry);
}
