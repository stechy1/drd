package cz.stechy.drd.controller.hero.creator;

import cz.stechy.drd.controller.hero.creator.HeroCreatorController3.ChoiceEntry;
import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.inventory.InventoryHelper;
import cz.stechy.drd.model.item.ItemBase;
import cz.stechy.drd.model.item.ItemRegistry;
import cz.stechy.screens.Bundle;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Knihovní třída obsahující konstanty pro dialog na nového hrdinu
 */
public final class HeroCreatorHelper {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CONVICTION = "conviction";
    public static final String RACE = "race";
    public static final String PROFESSION = "profession";
    public static final String LIVE = "live";
    public static final String STRENGTH = "strength";
    public static final String DEXTERITY = "dexterity";
    public static final String IMMUNITY = "immunity";
    public static final String INTELLIGENCE = "intelligence";
    public static final String CHARISMA = "charisma";
    public static final String HEIGHT = "height";
    public static final String INVENTORY = "inventory";

    private HeroCreatorHelper() {
        throw new AssertionError();
    }

    /**
     * Vytvoří novou postavu z bundle
     *
     * @param bundle {@link Bundle}
     * @return {@link Hero}
     */
    public static Hero fromBundle(final Bundle bundle) {
        return new Hero.Builder()
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .conviction(bundle.getInt(CONVICTION))
            .race(bundle.getInt(RACE))
            .profession(bundle.getInt(PROFESSION))
            .strength(bundle.getInt(STRENGTH))
            .dexterity(bundle.getInt(DEXTERITY))
            .immunity(bundle.getInt(IMMUNITY))
            .intelligence(bundle.getInt(INTELLIGENCE))
            .charisma(bundle.getInt(CHARISMA))
            .height(bundle.getInt(HEIGHT))
            .live(bundle.getInt(LIVE))
            .maxLive(bundle.getInt(LIVE))
            .build();
    }

    public static final class ItemEntry implements InventoryHelper.ItemRecord {

        private final StringProperty id = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
        private final IntegerProperty weight = new SimpleIntegerProperty();
        private final MaxActValue itemCount = new MaxActValue();

        public ItemEntry(ChoiceEntry entry) {
            final Optional<ItemBase> itemOptional = ItemRegistry.getINSTANCE()
                .getItemById(entry.id.get());
            if (!itemOptional.isPresent()) {
                return;
            }

            final ItemBase itemBase = itemOptional.get();
            this.id.setValue(itemBase.getId());
            this.name.setValue(itemBase.getName());
            this.weight.setValue(itemBase.getWeight());
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(itemBase.getImage());
            image.set(new Image(inputStream));
            itemCount.setMinValue(1);
            itemCount.setActValue(1);
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
}
