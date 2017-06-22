package cz.stechy.drd.model.item;

import cz.stechy.drd.model.IClonable;

/**
 * Třída reprezentující běžný předmět
 */
public class GeneralItem extends ItemBase {

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param item Kopírovaný item
     */
    private GeneralItem(GeneralItem item) {
        this(item.getId(), item.getAuthor(), item.getName(), item.getDescription(),
            item.getWeight(), item.getPrice().getRaw(), item.getImage(), item.isDownloaded(),
            item.isUploaded());
    }

    /**
     * Konstruktor pro běžný předmět
     *
     * @param id Id předmětu
     * @param author Autor předmětu
     * @param name Název předmětu
     * @param description Popis předmětu
     * @param weight Váha předmětu
     * @param price Cena předmětu
     * @param image Obrázek předmětu
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private GeneralItem(String id, String author, String name, String description, int weight,
        int price, byte[] image, boolean downloaded, boolean uploaded) {
        super(id, author, name, description, weight, price, image, downloaded, uploaded);
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
        return (T) new GeneralItem(this);
    }

    // endregion

    public static class Builder {

        private String id;
        private String author;
        private String name;
        private String description;
        private int weight;
        private int price;
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

        public GeneralItem build() {
            return new GeneralItem(id, author, name, description, weight, price, image, downloaded,
                uploaded);
        }
    }

}
