package cz.stechy.drd.util;

import cz.stechy.drd.model.MaxActValue;
import cz.stechy.drd.model.Money;
import cz.stechy.drd.model.WithImage;
import cz.stechy.drd.widget.MoneyWidget;
import java.io.ByteArrayInputStream;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Pomocná knihovní třída pro generování různých buněk
 */
public final class CellUtils {

    private static final int SLOT_SIZE = 40;

    /**
     * Pomocná metoda pro vygenerování tabulkové buňky, která obsahuje obrázek
     *
     * @param <S> Datový typ modelu tabulky
     * @return {@link TableCell}
     */
    public static <S> TableCell<S, Image> forImage() {
        final ImageView imageView = new ImageView();
        {
            imageView.setFitWidth(SLOT_SIZE);
            imageView.setFitHeight(SLOT_SIZE);
        }
        return new TableCell<S, Image>() {
            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(item);
                    setGraphic(imageView);
                }
            }
        };
    }

    /**
     * Pomocná metody pro vygenerování tabulkové buňky, která obsahuje editační pole pro zadání
     * hodnoty ze zadaného rozsahu
     *
     * @param <S> Datový typ modelu tabulky
     * @return {@link TableCell}
     */
    public static <S> TableCell<S, MaxActValue> forMaxActValue() {
        return forMaxActValue(null);
    }

    /**
     * Pomocná metody pro vygenerování tabulkové buňky, která obsahuje editační pole pro zadání
     * hodnoty ze zadaného rozsahu
     *
     * @param editable {@link BooleanProperty} Pomocná vlastnost, která nastavuje editovatelnost buňky
     * @param <S> Datový typ modelu tabulky
     * @return {@link TableCell}
     */
    public static <S> TableCell<S, MaxActValue> forMaxActValue(
        final BooleanProperty editable) {
        return new TableCell<S, MaxActValue>() {
            private final TextField input;
            private boolean initialized = false;
            private MaxActValue oldItem;

            {
                input = new TextField();
                if (editable != null) {
                    input.disableProperty().bind(editable);
                }
            }

            @Override
            public void updateItem(MaxActValue item, boolean empty) {
                super.updateItem(item, empty);
                // update according to new item
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    initialized = false;
                } else {
                    // Opravdu tu porovnávám přímo reference
                    if (oldItem != null && item != oldItem) {
                        FormUtils.disposeTextField(input, oldItem);
                        initialized = false;
                    }
                    if (!initialized) {
                        FormUtils.initTextFormater(input, item);
                        this.oldItem = item;
                        initialized = true;
                    }
                    setGraphic(input);
                    setText(null);
                }
            }
        };
    }

    /**
     * Pomocná metody pro vygenerování tabulkové buňky, která obsahuje grafickou reprezentaci peněz
     *
     * @param <S> Datový typ modelu tabulky
     * @return {@link TableCell}
     */
    public static <S> TableCell<S, Money> forMoney() {

        return new TableCell<S, Money>() {
            @Override
            protected void updateItem(Money item, boolean empty) {
                super.updateItem(item, empty);

                final MoneyWidget moneyWidget = new MoneyWidget();

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    moneyWidget.bind(item);
                    setGraphic(moneyWidget);
                }
            }
        };
    }

    /**
     * Pomocná metody pro vygenerování tabulkové buňky, která obsahuje váhy předmětů
     *
     * @param <S> Datový typ modelu tabulky
     * @return {@link TableCell}
     */
    public static <S> TableCell<S, Integer> forWeight() {
        return new TableCell<S, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item + " mn");
                }
            }
        };
    }

    /**
     * Generická třída pro buňky v listView, která obsahuje obrázek
     *
     * @param <T> Datový typ modelu listView
     */
    public static class RawImageListCell<T extends WithImage> extends ListCell<T> {

        final ImageView imageView = new ImageView();
        final Label label = new Label();
        final HBox container = new HBox(imageView, label);

        {
            imageView.setFitWidth(SLOT_SIZE);
            imageView.setFitHeight(SLOT_SIZE);

            label.setTextFill(Color.BLACK);
            label.setMinHeight(40);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setTextAlignment(TextAlignment.CENTER);

            container.setSpacing(8);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(item.getImage());
                imageView.setImage(new Image(inputStream));
                label.setText(item.toString());
                setGraphic(container);
            }
        }
    }
}
