package cz.stechy.drd.app.collections;

import cz.stechy.drd.model.item.ItemCollection;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

class ItemCollectionCell extends ListCell<ItemCollection> {

    @Override
    protected void updateItem(ItemCollection item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setText(item.getName());
        }
    }
}
