package cz.stechy.drd.controller.user;

import cz.stechy.drd.R;
import cz.stechy.drd.Context;
import cz.stechy.drd.model.persistent.UserService;
import cz.stechy.drd.model.persistent.UserService.UserException;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification.Length;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kontroler pro registraci uživatele
 */
public class RegisterController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnRegister;

    // endregion

    private final UserService userService;
    private final LoginModel loginModel = new LoginModel();

    private String title;
    private String registerFail;

    // endregion

    // region Constructors

    public RegisterController(Context context) {
        userService = context.getUserService();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.USER_REGISTER_TITLE);
        registerFail = resources.getString(R.Translate.NOTIFY_REGISTER_FAIL);

        txtLogin.textProperty().bindBidirectional(loginModel.login);
        txtPassword.textProperty().bindBidirectional(loginModel.password);
        btnRegister.disableProperty().bind(loginModel.valid.not());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(500, 160);
    }

    // region Button handlers
    @FXML
    private void handleRegister(ActionEvent actionEvent) {
        try {
            userService.register(loginModel.login.getValue(), loginModel.password.getValue());
            setResult(RESULT_SUCCESS);
            finish();
        } catch (UserException e) {
            logger.info("Registrace se nezdařila", e);
            showNotification(registerFail, Length.SHORT);
            loginModel.valid.set(false);
        }
    }

    @FXML
    private void handleBack(ActionEvent actionEvent) {
        back();
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        finish();
    }

    // endregion
}
