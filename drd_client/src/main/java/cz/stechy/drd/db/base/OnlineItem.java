package cz.stechy.drd.db.base;

import cz.stechy.drd.R.Translate;
import cz.stechy.drd.annotation.TranslateEntry;
import cz.stechy.drd.annotation.dao.Column;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Rozhraní pro všechny itemy, které můžou být nahrány do veřejné databáze
 */
public abstract class OnlineItem extends DatabaseItem {

    // region Variables

    // Autor entity
    @Column(columnName = "author", clazz = "String")
    @TranslateEntry(key = Translate.ENTRY_AUTHOR)
    protected final StringProperty author = new SimpleStringProperty(this, "author");
    // Příznak určující, zda-li je položka uložena v offline databázi, či nikoliv
    @Column(columnName = "downloaded", clazz = "Boolean")
    @TranslateEntry(key = Translate.ENTRY_DOWNLOADED)
    protected final BooleanProperty downloaded = new SimpleBooleanProperty(this, "downloaded",
        false);
    // Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
    @TranslateEntry(key = Translate.ENTRY_UPLOADED)
    private final BooleanProperty uploaded = new SimpleBooleanProperty(this, "uploaded", false);

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

    @Override
    public List<String> getDiffList(DatabaseItem other) {
        final List<String> diffList = super.getDiffList(other);
        final OnlineItem onlineItem = (OnlineItem) other;

        if (!Objects.equals(this.getAuthor(), onlineItem.getAuthor())) {
            diffList.add(author.getName());
        }

        return diffList;
    }

    // endregion

    // region Getters & Setters

    public boolean isDownloaded() {
        return downloaded.get();
    }

    public ReadOnlyBooleanProperty downloadedProperty() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded.set(downloaded);
    }

    public boolean isUploaded() {
        return uploaded.get();
    }

    public ReadOnlyBooleanProperty uploadedProperty() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded.set(uploaded);
    }

    public String getAuthor() {
        return author.get();
    }

    public ReadOnlyStringProperty authorProperty() {
        return author;
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    // endregion
}
