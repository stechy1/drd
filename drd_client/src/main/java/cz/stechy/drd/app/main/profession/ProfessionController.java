package cz.stechy.drd.app.main.profession;

import cz.stechy.drd.R;
import cz.stechy.drd.app.InjectableChild;
import cz.stechy.drd.app.main.MainScreen;
import cz.stechy.drd.model.entity.hero.Hero;
import cz.stechy.drd.model.entity.hero.Hero.Profession;
import cz.stechy.screens.BaseController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

public class ProfessionController implements MainScreen, Initializable, InjectableChild {

    // region Variables

    // region FXMl

    @FXML
    private StackPane container;

    // endregion

    private IProfessionController professionController;
    private BaseController parent;

    // endregion

    // region Constructors

    // endregion

    // region Private methods

    /**
     * Načte správný kontroler podle profese hrdiny
     *
     * @param hero {@link Hero}
     */
    private void loadProfession(final Hero hero) throws IOException {
        parent.getPartManager()
            .inContainer(container)
            .onLoaded((node, controller) -> ((IProfessionController) controller).setHero(hero))
            .show(getFxmlName(hero.getProfession()));
    }

    /**
     * Vytvoří instanci kontroleru a vrátí název FXML dokumentu, který se spáruje s touto instancí
     *
     * @param profession {@link Profession}
     * @return Název FXML dokumentu pro spárování s instanci kontroleru
     */
    private String getFxmlName(Profession profession) {
        String fxmlName = "";
        switch (profession) {
            case WARIOR:
                fxmlName = R.Fxml.WARRIOR;
                break;
            case RANGER:
                fxmlName = R.Fxml.RANGER;
                break;
            case ALCHEMIST:
                fxmlName = R.Fxml.ALCHEMIST;
                break;
            case WIZARD:
                fxmlName = R.Fxml.WIZARD;
                break;
            case THIEF:
                fxmlName = R.Fxml.THIEF;
                break;
        }

        return fxmlName;
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResourceBundle resources1 = resources;
    }

    @Override
    public void setHero(ReadOnlyObjectProperty<Hero> hero) {
        hero.addListener((observable, oldValue, newValue) -> {
            container.getChildren().clear();
            if (newValue == null || newValue.getName().isEmpty()) {
                return;
            }

            try {
                loadProfession(newValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void injectParent(BaseController parent) {
        this.parent = parent;
    }
}
