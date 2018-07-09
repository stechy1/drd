package cz.stechy.drd.model.chat;

import cz.stechy.drd.crypto.ICypher;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

/**
 * Třída reprezentující jeden kontakt v seznamu kontaktů v chatu
 */
public class ChatContact {

    // region Variables

    private final ObservableList<ChatMessageEntry> messages = FXCollections.observableArrayList();
    private final StringProperty name = new SimpleStringProperty(this, "name", null);
    private final String id;
    private final ObjectProperty<Color> contactColor = new SimpleObjectProperty<>(this, "contactColor", null);
    private final IntegerProperty unreadedMessages = new SimpleIntegerProperty(this, "unreadedMessages", 0);
    private final ICypher cypher;

    // endregion

    // region Constructors

    public ChatContact(String id, String name, ICypher cypher) {
        this.id = id;
        this.name.set(name);
        this.cypher = cypher;
        contactColor.set(Color.color(Math.random(), Math.random(), Math.random()));
    }

    // endregion

    // region Public methods

    /**
     * Zašifruje odchozí zprávu
     *
     * @param src Zpráva, která se má zašifrovat
     * @return Zašifrovaná zpráva
     */
    public byte[] encrypt(byte[] src) {
        return cypher.encrypt(src);
    }

    /**
     * Přidá zprávu do kolekce všech zpráv
     *
     * @param chatContact {@link ChatContact} Kontakt, od koho zpráva je
     * @param message Obsah zprávy
     */
    public void addMessage(ChatContact chatContact, String message) {
        messages.add(new ChatMessageEntry(chatContact, message));
        unreadedMessages.set(unreadedMessages.get() + 1);
    }

    // endregion

    // region Getters & Setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Color getColor() {
        return contactColor.get();
    }

    public ReadOnlyObjectProperty<Color> contactColorProperty() {
        return contactColor;
    }

    public ReadOnlyIntegerProperty unreadedMessagesProperty() {
        return unreadedMessages;
    }

    public ObservableList<ChatMessageEntry> getMessages() {
        return FXCollections.unmodifiableObservableList(messages);
    }

    // endregion

    @Override
    public String toString() {
        return getName();
    }
}
