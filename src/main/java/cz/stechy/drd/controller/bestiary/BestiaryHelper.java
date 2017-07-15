package cz.stechy.drd.controller.bestiary;

import cz.stechy.drd.model.db.base.Firebase.OnDeleteItem;
import cz.stechy.drd.model.db.base.Firebase.OnDownloadItem;
import cz.stechy.drd.model.db.base.Firebase.OnUploadItem;
import cz.stechy.drd.model.entity.mob.Mob;
import cz.stechy.drd.model.user.User;
import cz.stechy.screens.Bundle;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Pomocná knihovní třída k usnadnění práce s bestiářem
 */
public final class BestiaryHelper {

    // region Constants

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String AUTHOR = "author";
    public static final String IMAGE = "image";
    public static final String MOB_CLASS = "mob_class";
    public static final String RULES_TYPE = "rules_type";
    public static final String CONVICTION = "conviction";
    public static final String HEIGHT = "height";
    public static final String ATTACK = "attack";
    public static final String DEFENCE = "defence";
    public static final String VIABILITY = "viability";
    public static final String IMMUNITY = "immunity";
    public static final String METTLE = "mettle";
    public static final String VULNERABILITY = "vulnerability";
    public static final String MOBILITY = "mobility";
    public static final String PERSERVANCE = "perservance";
    public static final String CONTROL_ABILITY = "control_ability";
    public static final String INTELLIGENCE = "intelligence";
    public static final String CHARISMA = "charisma";
    public static final String BASIC_BOWER_OF_MIND = "basic_power_of_mind";
    public static final String EXPERIENCE = "experience";
    public static final String DOMESTICATION = "domestication";
    public static final String DOWNLOADED = "downloaded";
    public static final String UPLOADED = "uploaded";

    public static final String MOB_ACTION = "action_type";
    public static final int MOB_ACTION_ADD = 1;
    public static final int MOB_ACTION_UPDATE = 2;

    // endregion

    // region Constructors

    private BestiaryHelper() {}

    // endregion

    public static Bundle mobToBundle(Mob mob) {
        final Bundle bundle = new Bundle();

            bundle.putString(ID, mob.getId());
            bundle.putString(NAME, mob.getName());
            bundle.putString(DESCRIPTION, mob.getDescription());
            bundle.putString(AUTHOR, mob.getAuthor());
            bundle.put(IMAGE, mob.getImage());
            bundle.putInt(MOB_CLASS, mob.getMobClass().ordinal());
            bundle.putInt(RULES_TYPE, mob.getRulesType().ordinal());
            bundle.putInt(CONVICTION, mob.getConviction().ordinal());
            bundle.putInt(HEIGHT, mob.getHeight().ordinal());
            bundle.putInt(ATTACK, mob.getAttackNumber().getValue());
            bundle.putInt(DEFENCE, mob.getDefenceNumber());
            bundle.putInt(VIABILITY, mob.getViability());
            bundle.putInt(IMMUNITY, mob.getImmunity().getValue());
            bundle.putInt(METTLE, mob.getMettle());
            bundle.putInt(VULNERABILITY, mob.getVulnerability().value);
            bundle.putInt(MOBILITY, mob.getMobility());
            bundle.putInt(PERSERVANCE, mob.getPerservance());
            bundle.putInt(CONTROL_ABILITY, mob.getControlAbility());
            bundle.putInt(INTELLIGENCE, mob.getIntelligence().getValue());
            bundle.putInt(CHARISMA, mob.getCharisma().getValue());
            bundle.putInt(BASIC_BOWER_OF_MIND, mob.getBasicPowerOfMind());
            bundle.putInt(EXPERIENCE, mob.getExperience());
            bundle.putInt(DOMESTICATION, mob.getDomestication());
            bundle.putBoolean(DOWNLOADED, mob.isDownloaded());
            bundle.putBoolean(UPLOADED, mob.isUploaded());

        return bundle;
    }

    public static Mob mobFromBundle(Bundle bundle) {
        return new Mob.Builder()
            .id(bundle.getString(ID))
            .name(bundle.getString(NAME))
            .description(bundle.getString(DESCRIPTION))
            .author(bundle.getString(AUTHOR))
            .image(bundle.get(IMAGE))
            .mobClass(bundle.getInt(MOB_CLASS))
            .rulesType(bundle.getInt(RULES_TYPE))
            .conviction(bundle.getInt(CONVICTION))
            .height(bundle.getInt(HEIGHT))
            .attackNumber(bundle.getInt(ATTACK))
            .defenceNumber(bundle.getInt(DEFENCE))
            .viability(bundle.getInt(VIABILITY))
            .immunity(bundle.getInt(IMMUNITY))
            .mettle(bundle.getInt(METTLE))
            .vulnerability(bundle.getInt(VULNERABILITY))
            .mobility(bundle.getInt(MOBILITY))
            .perservance(bundle.getInt(PERSERVANCE))
            .controlAbility(bundle.getInt(CONTROL_ABILITY))
            .intelligence(bundle.getInt(INTELLIGENCE))
            .charisma(bundle.getInt(CHARISMA))
            .basicPowerOfMind(bundle.getInt(BASIC_BOWER_OF_MIND))
            .experience(bundle.getInt(EXPERIENCE))
            .domestication(bundle.getInt(DOMESTICATION))
            .downloaded(bundle.getBoolean(DOWNLOADED))
            .uploaded(bundle.getBoolean(UPLOADED))
            .build();
    }

    public static <T> TableCell<Mob, T> forActionButtons(final OnUploadItem<Mob> uploadHandler,
        final OnDownloadItem<Mob> downloadHandler, final OnDeleteItem<Mob> deleteHandler,
        User user, final ResourceBundle resources) {
        return new TableCell<Mob, T>() {
            final String resourceRemove = resources.getString("drd_firebase_entry_remove");
            final String resourceUpload = resources.getString("drd_firebase_entry_upload");
            final String resourceDownload = resources.getString("drd_firebase_entry_download");

            final Button btnRemote = new Button();
            final HBox container = new HBox(btnRemote);

            {
                btnRemote.setPrefHeight(15);
                btnRemote.setFont(Font.font(10));

                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    final Mob entry = getTableView().getItems().get(getIndex());

                    final ObjectProperty<EventHandler<ActionEvent>> downloadHandlerInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> uploadHandlerInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> deleteFromLocalDatabaseInternal = new SimpleObjectProperty<>();
                    final ObjectProperty<EventHandler<ActionEvent>> deleteFromRemoteDatabaseInternal = new SimpleObjectProperty<>();

                    downloadHandlerInternal.setValue(event -> {
                        if (downloadHandler != null) {
                            downloadHandler.onDownloadRequest(entry);
                        }
                    });

                    uploadHandlerInternal.setValue(event -> {
                        if (uploadHandler != null) {
                            uploadHandler.onUploadRequest(entry);
                        }
                    });

                    deleteFromLocalDatabaseInternal.setValue(event -> {
                        if (deleteHandler != null) {
                            deleteHandler.onDeleteRequest(entry, false);
                        }
                    });

                    deleteFromRemoteDatabaseInternal.setValue(event -> {
                        if (deleteHandler != null) {
                            deleteHandler.onDeleteRequest(entry, true);
                        }
                    });

                    btnRemote.textProperty().bind(Bindings
                        .when(entry.authorProperty()
                            .isEqualTo(user.nameProperty())) // Autor jsem já
                        .then(Bindings
                            .when(entry.uploadedProperty())
                            .then(resourceRemove)
                            .otherwise(resourceUpload))
                        .otherwise(Bindings
                            .when(entry.downloadedProperty())
                            .then(resourceRemove)
                            .otherwise(resourceDownload)));
                    btnRemote.onActionProperty().bind(Bindings
                        .when(entry.authorProperty()
                            .isEqualTo(user.nameProperty())) // Autor jsem já
                        .then(Bindings
                            .when(entry.uploadedProperty())
                            .then(deleteFromRemoteDatabaseInternal)
                            .otherwise(uploadHandlerInternal))
                        .otherwise(Bindings
                            .when(entry.downloadedProperty())
                            .then(deleteFromLocalDatabaseInternal)
                            .otherwise(downloadHandlerInternal)));
                    btnRemote.disableProperty().bind(user.loggedProperty().not());
                    setAlignment(Pos.BASELINE_CENTER);
                    setGraphic(container);
                }
            }
        };
    }
}
