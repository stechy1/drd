package cz.stechy.drd.controller.spellbook;

import javafx.util.Pair;

public interface ISpellGraphNode {

    Pair<ISpellGraphNode, ISpellGraphNode> getParentNodes();

    ISpellGraphNode getChildNode();

}
