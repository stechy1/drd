package cz.stechy.drd.firebase;

@FunctionalInterface
public interface ItemEventListener {
    void onEvent(ItemEvent event);

}
