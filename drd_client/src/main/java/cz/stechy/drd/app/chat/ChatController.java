package cz.stechy.drd.app.chat;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeView;
import cz.stechy.drd.R;
import cz.stechy.drd.model.User;
import cz.stechy.drd.model.chat.ChatContact;
import cz.stechy.drd.model.chat.OnChatMessageReceived;
import cz.stechy.drd.service.ChatService;
import cz.stechy.drd.service.UserService;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ChatController extends BaseController implements Initializable {

    // region Variables

    // region FXML

    @FXML
    private JFXListView<ChatContact> listContacts;
    @FXML
    private JFXTreeView treeRooms;
    @FXML
    private TabPane tabConversations;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnSend;

    // endregion

    private final ChatService chatService;

    private User user;
    private String title;

    // endregion

    // region Constructors

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        user = userService.getUser();
    }

    // endregion

    // region Private methods

    /**
     * Vytvoří nový tab se jménem uživatele
     *
     * @param name Jméno uživatele, se kterým si budu psát
     * @return {@link Tab}
     */
    private ChatTab makeNewTab(String id, String name) {
        final ChatTab chatTab = new ChatTab(name);
        chatTab.setUserData(id);
        return chatTab;
    }

    /**
     * Zobrazí tab s vybranou konverzací
     *
     * @param contact {@link ChatContact}
     */
    private ChatTab showConversation(ChatContact contact) {
        final Optional<ChatTab> optionalTab = tabConversations.getTabs()
            .stream()
            .filter(tab -> tab.getText().equals(contact.getName()))
            .map(tab -> (ChatTab) tab)
            .findFirst();

        if (optionalTab.isPresent()) {
            tabConversations.getSelectionModel().select(optionalTab.get());
            return optionalTab.get();
        } else {
            final ChatTab newTab = makeNewTab(contact.getId(), contact.getName());
            tabConversations.getTabs().add(newTab);
            return newTab;
        }
    }

    /**
     * Zobrazí tab s vybranou konverzací a připojí text
     *
     * @param id Id klienta, se kterým se komunikuje
     * @param message Obsah zprávy, který se má připojit
     */
    private void showConversationWithText(String id, String message) {
        final ChatContact contact = chatService.getContactById(id);
        final ChatTab chatTab = showConversation(contact);
        appendText(chatTab, message, false, contact.getName());
    }

    /**
     * Vloží zprávu do vybraného tabu
     *  @param chatTab {@link ChatTab} Okno s komunikaci s klientem
     * @param message Obsah zprávy, který se má připojit
     * @param fromMe True, pokud jsem zprávu odeslal já. False, pokud jsem zprávu přijal
     */
    private void appendText(ChatTab chatTab, String message, boolean fromMe, String userName) {
        chatTab.appendText(message, fromMe, userName);
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.CHAT_TITLE);

        chatService.addChatMessageReceivedListener(this.chatMessageReceived);

        listContacts.setOnMouseClicked(this.listContactsClick);
        btnSend.disableProperty().bind(tabConversations.getSelectionModel().selectedItemProperty().isNull());

        ObservableMergers.listObserveMap(chatService.getClients(), listContacts.getItems());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(600, 350);
    }

    @Override
    protected void onClose() {
        chatService.removeChatMessageReceivedListener(this.chatMessageReceived);
    }

    // region Button handlers

    @FXML
    private void handleNewRoom(ActionEvent actionEvent) {

    }

    @FXML
    private void handleSendMessage(ActionEvent actionEvent) {
        final ChatTab tab = (ChatTab) tabConversations.getSelectionModel().getSelectedItem();
        final String id = (String) tab.getUserData();
        final String message = txtMessage.getText();
        chatService.sendMessage(id, message);
        appendText(tab, message, true, user.getName());
    }

    // endregion

    private final OnChatMessageReceived chatMessageReceived = (message, source) -> {
        final Optional<ChatTab> chatTabOptional = tabConversations.getTabs()
            .stream()
            .filter(tab -> {
                final String id = (String) tab.getUserData();
                return source.equals(id);
            })
            .map(tab -> (ChatTab) tab)
            .findFirst();
        if (chatTabOptional.isPresent()) {
            appendText(chatTabOptional.get(), message, false, chatTabOptional.get().getText());
        } else {
            showConversationWithText(source, message);
        }
    };

    private final EventHandler<? super MouseEvent> listContactsClick = event -> {
        final int clickCount = event.getClickCount();
        if (clickCount != 2) {
            return;
        }

        final ChatContact contact = listContacts.getSelectionModel().getSelectedItem();
        if (contact == null) {
            return;
        }

        showConversation(contact);
    };
}
