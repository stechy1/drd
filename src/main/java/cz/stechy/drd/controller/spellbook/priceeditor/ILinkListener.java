package cz.stechy.drd.controller.spellbook.priceeditor;

import javafx.geometry.Point2D;

/**
 * Rozhraní pro práci s drag&drop
 */
public interface ILinkListener {

    /**
     * Uloží referenci zdrojového nodu pro pozdější využití
     *  @param node {@link DraggableSpellNode}
     *
     */
    void saveSourceNode(DraggableSpellNode node);

    /**
     * Vytvoří nový {@link NodeLink} s jedním koncem na startovní pozici
     *
     * @param start {@link Point2D} Startovní pozice linku
     * @return {@link NodeLink}
     */
    NodeLink createNodeLink(Point2D start);

    /**
     * Propojí konec čáry s cílovým nodem
     *
     * @param id Id cílového nodu
     * @param position Pozice cílového kolečka
     */
    void connectLineEndWithNode(String id, LinkPosition position);
}
