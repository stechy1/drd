package cz.stechy.drd.model.shop;

/**
 * Rozhraní pro přístup k metodě, která se zavolá, když se odebere položka z košíku.
 *
 * @param <S> {@link cz.stechy.drd.model.shop.entry.ShopEntry}
 */
@FunctionalInterface
public interface OnRemoveItemFromCart<S> {

    void onRemove(S entry);
}
