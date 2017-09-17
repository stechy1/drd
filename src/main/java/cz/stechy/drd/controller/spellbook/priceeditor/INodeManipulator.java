package cz.stechy.drd.controller.spellbook.priceeditor;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;

/**
 * Rozhraní definující metody pro manipulaci s nody, jako přidat a odebrat
 */
interface INodeManipulator {

    /**
     * Přidá node na scénu
     *
     * @param node {@link Node)}
     */
    void addNode(Node node);

    /**
     * Odebere node ze scény
     *
     * @param node {@link Node}
     */
    void removeNode(Node node);

    /**
     * Nastaví onDragOverHandler.
     * Pokud null, nastaví se výchozí handler
     *
     * @param event {@link EventHandler<? super DragEvent}
     */
    void setOnDragOverHandler(EventHandler<? super DragEvent> event);
}
