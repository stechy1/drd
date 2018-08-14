package cz.stechy.drd.model.chat;

/**
 * Třída reprezentující jednu zprávu
 */
public final class ChatMessageEntry {

    // region Variables

    private final ChatContact chatContact;
    private final String message;

    // endregion

    // region Constructors

    ChatMessageEntry(ChatContact chatContact, String message) {
        this.chatContact = chatContact;
        this.message = message;
    }

    // endregion

    // region Getters & Setters

    public ChatContact getChatContact() {
        return chatContact;
    }

    public String getMessage() {
        return message;
    }

    // endregion
}
