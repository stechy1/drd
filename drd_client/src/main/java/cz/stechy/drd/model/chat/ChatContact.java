package cz.stechy.drd.model.chat;

import cz.stechy.drd.crypto.ICypher;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Třída reprezentující jeden kontakt v seznamu kontaktů v chatu
 */
public class ChatContact {

    // region Variables

    private final ObservableList<ChatMessageEntry> messages = FXCollections.observableArrayList();
    private final StringProperty name = new SimpleStringProperty(this, "name", null);
    private final String id;
    private final ICypher cypher;

    // endregion

    // region Constructors

    public ChatContact(String id, String name, ICypher cypher) {
        this.id = id;
        this.name.set(name);
        this.cypher = cypher;
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

    public ObservableList<ChatMessageEntry> getMessages() {
        return FXCollections.unmodifiableObservableList(messages);
    }

    // endregion

    @Override
    public String toString() {
        return getName();
    }
}
