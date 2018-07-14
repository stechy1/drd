package cz.stechy.drd.app.chat;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTreeView;
import cz.stechy.drd.R;
import cz.stechy.drd.model.chat.ChatContact;
import cz.stechy.drd.model.chat.OnChatMessageReceived;
import cz.stechy.drd.service.ChatService;
import cz.stechy.drd.util.ObservableMergers;
import cz.stechy.screens.BaseController;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
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

    private ChatService chatService;
    private String title;

    // endregion

    // region Constructors

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // endregion

    // region Private methods

    /**
     * Vytvoří nový tab se jménem uživatele
     *
     * @param chatContact {@link ChatContact}
     * @return {@link Tab}
     */
    private ChatTab makeNewTab(ChatContact chatContact) {
        final ChatTab chatTab = new ChatTab(chatContact);
        chatTab.setUserData(chatContact.getId());
        return chatTab;
    }

    /**
     * Zobrazí tab s vybranou konverzací
     *
     * @param contact {@link ChatContact}
     */
    private void showConversation(ChatContact contact) {
        final Optional<ChatTab> optionalTab = tabConversations.getTabs()
            .stream()
            .filter(tab -> tab.getText().equals(contact.getName()))
            .map(tab -> (ChatTab) tab)
            .findFirst();

        if (optionalTab.isPresent()) {
            tabConversations.getSelectionModel().select(optionalTab.get());
        } else {
            tabConversations.getTabs().add(makeNewTab(contact));
        }
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.CHAT_TITLE);

        chatService.addChatMessageReceivedListener(this.chatMessageReceived);

        listContacts.setCellFactory(param -> new ChatListViewEntry());
        listContacts.setOnMouseClicked(this.listContactsClick);
        final BooleanBinding canSendMessage = tabConversations.getSelectionModel()
            .selectedItemProperty().isNull();
        btnSend.disableProperty().bind(canSendMessage);
        txtMessage.disableProperty().bind(canSendMessage);
        txtMessage.textProperty().addListener(this.messageContentListener);

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
        txtMessage.clear();
    }

    // endregion

    private final OnChatMessageReceived chatMessageReceived = (message, source) -> {
        // Use case:
        // 1. Přijde zpráva
        // 2. pokud je otevřený tab, do kterého patří zpráva
        //    + přidej text a skonči
        //    - zobraz notifikaci o příchozí zprávě, inkrementuj indikátor u příslušného kontaktu a zprávu ulož
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

    private ChangeListener<? super String> messageContentListener = (observable, oldValue, newValue) -> {
        final ChatTab tab = (ChatTab) tabConversations.getSelectionModel().getSelectedItem();
        final String id = (String) tab.getUserData();
        chatService.notifyTyping(id, !newValue.isEmpty());
    };
}
