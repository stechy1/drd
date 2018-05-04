package cz.stechy.drd.app.spellbook.priceeditor;

import cz.stechy.drd.app.spellbook.priceeditor.node.DraggableSpellNode;
import cz.stechy.drd.app.spellbook.priceeditor.node.NodeLink;
import javafx.geometry.Point2D;

/**
 * Rozhraní pro práci s drag&drop
 */
public interface ILinkListener {

    /**
     * Uloží referenci zdrojového nodu pro pozdější využití
     *
     * @param node {@link DraggableSpellNode}
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
     * ULoží {@link NodeLink} pro pozdější použití
     *
     * @param nodeLink {@link NodeLink}
     */
    void saveNodeLink(NodeLink nodeLink);

    /**
     * Odebere {@link NodeLink} ze scény
     *
     * @param nodeLink {@link NodeLink}
     */
    void deleteNodeLink(NodeLink nodeLink);

    /**
     * Propojí konec čáry s cílovým nodem
     *
     * @param id Id cílového nodu
     * @param position Pozice cílového kolečka
     */
    void connectLineEndWithNode(String id, LinkPosition position);
}
