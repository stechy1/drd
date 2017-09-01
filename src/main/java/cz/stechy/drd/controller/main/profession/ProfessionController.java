package cz.stechy.drd.controller.main.profession;

import cz.stechy.drd.R;
import cz.stechy.drd.controller.InjectableChild;
import cz.stechy.drd.controller.main.MainScreen;
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
    private ResourceBundle resources;
    private BaseController parent;

    // endregion

    // region Constructors

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
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
                fxmlName = R.FXML.WARRIOR;
                break;
            case RANGER:
                fxmlName = R.FXML.RANGER;
                break;
            case ALCHEMIST:
                fxmlName = R.FXML.ALCHEMIST;
                break;
            case WIZARD:
                fxmlName = R.FXML.WIZARD;
                break;
            case THIEF:
                fxmlName = R.FXML.THIEF;
                break;
        }

        return fxmlName;
    }
}
