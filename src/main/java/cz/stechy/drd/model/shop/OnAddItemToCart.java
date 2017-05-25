package cz.stechy.drd.model.shop;

/**
 *
 * @param <S>
 */
@FunctionalInterface
public interface OnAddItemToCart<S> {

    void onAdd(S entry);
}
