package cz.stechy.drd.model.inventory;

import java.io.Serializable;
import javafx.scene.input.DataFormat;

/**
 * Kontainer pro Drag & Drop
 */
public class DragItemContainer implements Serializable {

    public static final DataFormat MOVE_ITEM = new DataFormat("move_item");

    public final String id;
    public final int ammount;

    public DragItemContainer(String id, int ammount) {
        this.id = id;
        this.ammount = ammount;
    }


}
