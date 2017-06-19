package cz.stechy.drd.model.db.base;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Rozhraní pro všechny itemy, které můžou být nahrány do veřejné databáze
 */
public abstract class OnlineItem extends DatabaseItem {

    // region Variables

    // Autor entity
    protected final StringProperty author = new SimpleStringProperty();
    // Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
    protected final BooleanProperty downloaded = new SimpleBooleanProperty(false);
    // Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
    private final BooleanProperty uploaded = new SimpleBooleanProperty(false);

    // endregion

    // region Constructors

    /**
     * Konstruktor pro každý online databázový item
     *
     * @param id Id předmětu
     * @param author Autor předmětu
     * @param downloaded Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    protected OnlineItem(String id, String author, boolean downloaded, boolean uploaded) {
        super(id);

        this.author.set(author);
        this.downloaded.set(downloaded);
        this.uploaded.set(uploaded);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);
        OnlineItem onlineItem = (OnlineItem) other;
        this.downloaded.set(onlineItem.isDownloaded());
        this.uploaded.set(onlineItem.isUploaded());
    }

    // endregion

    // region Getters & Setters

    public boolean isDownloaded() {
        return downloaded.get();
    }

    public BooleanProperty downloadedProperty() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded.set(downloaded);
    }

    public boolean isUploaded() {
        return uploaded.get();
    }

    public BooleanProperty uploadedProperty() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded.set(uploaded);
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

    // endregion
}
