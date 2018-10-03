package cz.stechy.drd.app.collections;

import cz.stechy.drd.model.item.OnlineCollection;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

class ItemCollectionCell extends ListCell<OnlineCollection> {

    @Override
    protected void updateItem(OnlineCollection item, boolean empty) {
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
