package cz.stechy.drd.controller.spellbook;

import cz.stechy.drd.model.spell.price.ISpellPrice;
import javafx.util.Pair;

public interface ISpellGraphNode {

    Pair<ISpellGraphNode, ISpellGraphNode> getParentNodes();

    ISpellGraphNode getChildNode();

    ISpellPrice getPrice();

}
