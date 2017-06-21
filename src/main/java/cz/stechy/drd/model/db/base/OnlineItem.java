package cz.stechy.drd.model.db.base;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * Rozhraní pro všechny itemy, které můžou být nahrány do veřejné databáze
 */
public abstract class OnlineItem extends DatabaseItem {

    // region Variables

    // Autor entity
    protected final ReadOnlyStringWrapper author = new ReadOnlyStringWrapper();
    // Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
    protected final ReadOnlyBooleanWrapper downloaded = new ReadOnlyBooleanWrapper(false);
    // Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
    private final ReadOnlyBooleanWrapper uploaded = new ReadOnlyBooleanWrapper(false);

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

        setAuthor(author);
        setDownloaded(downloaded);
        setUploaded(uploaded);
    }

    // endregion

    // region Public methods

    @Override
    public void update(DatabaseItem other) {
        super.update(other);

        OnlineItem onlineItem = (OnlineItem) other;
        setDownloaded(onlineItem.isDownloaded());
        setUploaded(onlineItem.isUploaded());
    }

    // endregion

    // region Getters & Setters

    public boolean isDownloaded() {
        return downloaded.get();
    }

    public ReadOnlyBooleanProperty downloadedProperty() {
        return downloaded.getReadOnlyProperty();
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded.set(downloaded);
    }

    public boolean isUploaded() {
        return uploaded.get();
    }

    public ReadOnlyBooleanProperty uploadedProperty() {
        return uploaded.getReadOnlyProperty();
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded.set(uploaded);
    }

    public String getAuthor() {
        return author.get();
    }

    public ReadOnlyStringProperty authorProperty() {
        return author.getReadOnlyProperty();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    // endregion
}
