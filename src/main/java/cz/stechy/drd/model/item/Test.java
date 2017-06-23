package cz.stechy.drd.model.item;

import cz.stechy.drd.model.IClonable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Test extends ItemBase {

    // region Variables

    protected final IntegerProperty id2 = new SimpleIntegerProperty();
    protected final StringProperty name2 = new SimpleStringProperty();
    protected final BooleanProperty downloaded2 = new SimpleBooleanProperty();
    protected final ObjectProperty<byte[]> blob_type2 = new SimpleObjectProperty();

    // endregion

    // region Constructors

    public Test(Test item) {
        this(item.getId(), item.getName(), item.getDescription(), item.getAuthor(),
            item.getWeight(), item.getPrice().getRaw(), item.getId2(), item.getName2(),
            item.getDownloaded2(), item.getBlob_Type2(),
            item.getImage(), item.isDownloaded(), item.isUploaded());
    }

    public Test(String id, String author, String name, String description, int weight,
        int price, int id2, String name2, boolean downloaded2, byte[] blob_type2, byte[] image,
        boolean downloaded, boolean uploaded) {
        super(id, author, name, description, weight, price, image, , downloaded, uploaded);

        this.id2.setValue(id2);
        this.name2.setValue(name2);
        this.downloaded2.setValue(downloaded2);
        this.blob_type2.setValue(blob_type2);

    }

    // endregion

    // region Getters & Setters

    public Integer getId2() {
        return id2.get();
    }

    public IntegerProperty id2Property() {
        return id2;
    }

    public void setId2(Integer id2) {
        this.id2.set(id2);
    }

    public String getName2() {
        return name2.get();
    }

    public StringProperty name2Property() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2.set(name2);
    }

    public Boolean getDownloaded2() {
        return downloaded2.get();
    }

    public BooleanProperty downloaded2Property() {
        return downloaded2;
    }

    public void setDownloaded2(Boolean downloaded2) {
        this.downloaded2.set(downloaded2);
    }

    public byte[] getBlob_Type2() {
        return blob_type2.get();
    }

    public ObjectProperty<byte[]> blob_type2Property() {
        return blob_type2;
    }

    public void setBlob_Type2(byte[] blob_type2) {
        this.blob_type2.set(blob_type2);
    }

    // endregion

    // region Public methods

    @Override
    public ItemType getItemType() {
        return ItemType.GENERAL;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IClonable> T duplicate() {
        return (T) new Test(this);
    }

    // endregion

    public static class Builder {

        private String id;
        private String author;
        private String name;
        private String description;
        private int weight;
        private int price;
        private int id2;
        private String name2;
        private boolean downloaded2;
        private byte[] blob_type2;

        private byte[] image;
        private boolean downloaded;
        private boolean uploaded;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder id2(int id2) {
            this.id2 = id2;
            return this;
        }

        public Builder name2(String name2) {
            this.name2 = name2;
            return this;
        }

        public Builder downloaded2(boolean downloaded2) {
            this.downloaded2 = downloaded2;
            return this;
        }

        public Builder blob_type2(byte[] blob_type2) {
            this.blob_type2 = blob_type2;
            return this;
        }


        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder downloaded(boolean downloaded) {
            this.downloaded = downloaded;
            return this;
        }

        public Builder uploaded(boolean uploaded) {
            this.uploaded = uploaded;
            return this;
        }

        public Test build() {
            return new Test(id, author, name, description, weight, price, id2, name2, downloaded2,
                blob_type2, image, downloaded,
                uploaded);
        }
    }

}
