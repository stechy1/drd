package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.drd.model.Context;
import cz.stechy.drd.model.Rule;
import cz.stechy.drd.model.entity.Conviction;
import cz.stechy.drd.model.entity.Height;
import cz.stechy.drd.model.entity.mob.Mob.MobClass;
import cz.stechy.drd.util.StringConvertors;
import cz.stechy.drd.util.Translator;
import cz.stechy.screens.Bundle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Kontroler pro editaci základních vlastností nestvůry
 */
public class BestiaryEditGeneralController implements Initializable, IEditController {

    // region Constants

    private static final String NAME = "name";
    private static final String RULE = "rule";
    private static final String MOB_CLASS = "mob_class";
    private static final String CONVICTION = "conviction";
    private static final String HEIGHT = "height";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private TextField txtName;
    @FXML
    private ComboBox<Rule> cmbRule;
    @FXML
    private ComboBox<MobClass> cmbMobClass;
    @FXML
    private ComboBox<Conviction> cmbConviction;
    @FXML
    private ComboBox<Height> cmbHeight;

    // endregion

    private final Model model = new Model();
    private final Translator translator;

    // endregion

    // region Constructors

    public BestiaryEditGeneralController(Context context) {
        this.translator = context.getTranslator();
    }

    // endregion

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbRule.setConverter(StringConvertors.forRulesType(translator));
        cmbMobClass.setConverter(StringConvertors.forMobClass(translator));
        cmbConviction.setConverter(StringConvertors.forConvictionConverter(translator));
    }

    @Override
    public void loadMobPropertiesFromBundle(Bundle bundle) {
        model.name.setValue(bundle.getString(NAME));
        model.rule.setValue(Rule.values()[bundle.getInt(RULE)]);
        model.mobClass.setValue(MobClass.values()[bundle.getInt(MOB_CLASS)]);
        model.conviction.setValue(Conviction.values()[bundle.getInt(CONVICTION)]);
        model.height.setValue(Height.values()[bundle.getInt(HEIGHT)]);
    }

    @Override
    public void saveMobPropertiesToBundle(Bundle bundle) {
        bundle.putString(NAME, model.name.getValue());
        bundle.putInt(RULE, model.rule.getValue().ordinal());
        bundle.putInt(MOB_CLASS, model.mobClass.getValue().ordinal());
        bundle.putInt(CONVICTION, model.conviction.getValue().ordinal());
        bundle.putInt(HEIGHT, model.height.getValue().ordinal());
    }

    private static final class Model {
        private final StringProperty name = new SimpleStringProperty(this, "name");
        private final ObjectProperty<Rule> rule = new SimpleObjectProperty<>(this, "rule");
        private final ObjectProperty<MobClass> mobClass = new SimpleObjectProperty<>(this, "mobClass");
        private final ObjectProperty<Conviction> conviction = new SimpleObjectProperty<>(this, "conviction");
        private final ObjectProperty<Height> height = new SimpleObjectProperty<>(this, "height");
    }
}
