package cz.stechy.drd.app.hero.creator;

import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.util.DialogUtils.ChoiceEntry;
import java.io.ByteArrayInputStream;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public final class ItemEntry implements InventoryHelper.ItemRecord {

    private final ItemBase itemBase;
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private final IntegerProperty weight = new SimpleIntegerProperty();
    private final MaxActValue itemCount = new MaxActValue();

    ItemEntry(ChoiceEntry entry) {
        this.itemBase = (ItemBase) entry.getBase();
        this.id.setValue(this.itemBase.getId());
        this.name.setValue(this.itemBase.getName());
        this.weight.setValue(this.itemBase.getWeight());
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(this.itemBase.getImage());
        image.set(new Image(inputStream));
        itemCount.setMinValue(1);
        itemCount.setActValue(1);
    }

    @Override
    public ItemBase getItemBase() {
        return itemBase;
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public MaxActValue getItemCount() {
        return itemCount;
    }

    @Override
    public int getAmmount() {
        return itemCount.getActValue().intValue();
    }
}
