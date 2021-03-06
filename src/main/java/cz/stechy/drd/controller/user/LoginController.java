package cz.stechy.drd.controller.user;

import cz.stechy.drd.R;
import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.persistent.UserManager;
import cz.stechy.drd.model.persistent.UserManager.UserException;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Bundle;
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
 * Kontroler pro přihlášení
 */
public class LoginController extends BaseController implements Initializable {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final int ACTION_REGISTRATION = 1;
    private static final int ACTION_LOST_PASSWORD = 2;

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Button btnLogin;
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtPassword;

    // endregion

    private final LoginModel loginModel = new LoginModel();
    private final UserManager userManager;

    private String title;

    // endregion

    // region Constructors

    public LoginController(Context context) {
        userManager = context.getUserManager();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title = resources.getString(R.Translate.USER_LOGIN_TITLE);

        txtLogin.textProperty().bindBidirectional(loginModel.login);
        txtPassword.textProperty().bindBidirectional(loginModel.password);
        btnLogin.disableProperty().bind(loginModel.valid.not());
    }

    @Override
    protected void onResume() {
        setTitle(title);
        setScreenSize(500, 160);
    }

    @Override
    protected void onScreenResult(int statusCode, int actionId, Bundle bundle) {
        switch (actionId) {
            case ACTION_REGISTRATION:
                if (statusCode != RESULT_SUCCESS) {
                    return;
                }

                break;
            case ACTION_LOST_PASSWORD:
                break;
            default:

        }
    }

    // region Button handlers
    public void handleLogin(ActionEvent actionEvent) {
        try {
            userManager.login(loginModel.login.getValue(), loginModel.password.getValue());
            setResult(RESULT_SUCCESS);
            finish();
        } catch (UserException e) {
            logger.info("Přihlášení se nezdařilo", e);
            loginModel.valid.set(false);
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        finish();
    }

    public void handleRegistration(ActionEvent actionEvent) {
        startNewDialogForResult(R.FXML.REGISTER, ACTION_REGISTRATION);
    }

    public void handleLostPassword(ActionEvent actionEvent) {
        //startScreenForResult("lost_password", ACTION_LOST_PASSWORD);
    }

    // endregion

}
