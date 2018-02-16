package cz.stechy.drd.app.user;

import cz.stechy.drd.R;
import cz.stechy.drd.dao.UserDao;
import cz.stechy.screens.BaseController;
import cz.stechy.screens.Notification;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

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

    private final UserDao userDao;
    private final LoginModel loginModel = new LoginModel();

    private String title;
    private String registerFail;

    // endregion

    // region Constructors

    public RegisterController(UserDao userDao) {
        this.userDao = userDao;
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
        setScreenSize(400, 260);
    }

    // region Button handlers
    @FXML
    private void handleRegister(ActionEvent actionEvent) {
        userDao.registerAsync(loginModel.login.getValue(), loginModel.password.getValue(), (error, ref) -> {
            if (error != null) {
                LOGGER.info("Registrace se nezdařila");
                showNotification(new Notification(registerFail));
                loginModel.valid.set(false);
            } else {
                setResult(RESULT_SUCCESS);
                finish();
            }
        });
    }

    // endregion
}
