package cz.stechy.drd.controller.spellbook.priceeditor;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;

/**
 * Rozhraní definující metody pro manipulaci s nody, jako přidat a odebrat
 */
interface INodeManipulator {

    /**
     * Nastaví onDragOverHandler.
     * Pokud null, nastaví se výchozí handler
     *
     * @param event {@link EventHandler<? super DragEvent}
     */
    void setOnDragOverHandler(EventHandler<? super DragEvent> event);

    /**
     * Odebere {@link DraggableSpellNode} z plochy
     *
     * @param node {@link DraggableSpellNode}
     */
    void removeNode(DraggableSpellNode node);

}
