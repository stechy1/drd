package cz.stechy.drd.model.shop;

/**
 *
 *
 * @param <S>
 */
@FunctionalInterface
public interface OnRemoveItemFromCart<S> {

    void onRemove(S entry);
}
