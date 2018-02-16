package cz.stechy.drd.app.bestiary;

import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import java.io.ByteArrayInputStream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Třída reprezentující jeden záznam v tabulce bestiáře
 */
public final class MobEntry {

    // region Variables

    private final ObjectProperty<byte[]> imageRaw = new SimpleObjectProperty<>(this, "imageRaw");
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>(this, "image");
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final StringProperty author = new SimpleStringProperty(this, "author");
    private final ObjectProperty<MobClass> mobClass = new SimpleObjectProperty<>(this, "mobClass");
    private final ObjectProperty<Rule> rulesType = new SimpleObjectProperty<>(this, "rulesType");
    private final IntegerProperty viability = new SimpleIntegerProperty(this, "viability");
    private final BooleanProperty downloaded = new SimpleBooleanProperty(this, "downloaded");
    private final BooleanProperty uploaded = new SimpleBooleanProperty(this, "uploaded");

    private final Mob mobBase;

    // endregion

    // region Constructors

    public MobEntry(Mob mobBase) {
        this.mobBase = mobBase;

        this.imageRaw.bind(mobBase.imageProperty());
        this.name.bind(mobBase.nameProperty());
        this.author.bind(mobBase.authorProperty());
        this.mobClass.bind(mobBase.mobClassProperty());
        this.rulesType.bind(mobBase.rulesTypeProperty());
        this.viability.bind(mobBase.viabilityProperty());
        this.downloaded.bind(mobBase.downloadedProperty());
        this.uploaded.bind(mobBase.uploadedProperty());


        image.bind(Bindings.createObjectBinding(() -> {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(mobBase.getImage());
            return new Image(inputStream);
        }, imageRaw));
    }

    // endregion

    // region Getters & Setters

    public Image getImage() {
        return image.get();
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
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

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public MobClass getMobClass() {
        return mobClass.get();
    }

    public ObjectProperty<MobClass> mobClassProperty() {
        return mobClass;
    }

    public void setMobClass(MobClass mobClass) {
        this.mobClass.set(mobClass);
    }

    public Rule getRulesType() {
        return rulesType.get();
    }

    public ObjectProperty<Rule> rulesTypeProperty() {
        return rulesType;
    }

    public void setRulesType(Rule rulesType) {
        this.rulesType.set(rulesType);
    }

    public int getViability() {
        return viability.get();
    }

    public IntegerProperty viabilityProperty() {
        return viability;
    }

    public void setViability(int viability) {
        this.viability.set(viability);
    }

    public boolean isDownloaded() {
        return downloaded.get();
    }

    public BooleanProperty downloadedProperty() {
        return downloaded;
    }

    public boolean isUploaded() {
        return uploaded.get();
    }

    public BooleanProperty uploadedProperty() {
        return uploaded;
    }

    public Mob getMobBase() {
        return mobBase;
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MobEntry mobEntry = (MobEntry) o;

        return mobBase.equals(mobEntry.mobBase);
    }

    @Override
    public int hashCode() {
        return mobBase.hashCode();
    }
}
