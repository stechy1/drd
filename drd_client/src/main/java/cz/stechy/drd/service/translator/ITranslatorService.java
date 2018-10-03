package cz.stechy.drd.service.translator;

import cz.stechy.drd.service.translator.TranslatorService.Key;
import java.util.List;
import java.util.Map;
import javafx.util.StringConverter;

public interface ITranslatorService {

    /**
     * Získá kolekci s přeloženými konstantami podle zvoleného klíče
     *
     * @param key {@link Key}
     * @return Přeloženou kolekci
     */
    List<String> getTranslationFor(Key key);

    /**
     * Vrátí jeden přeložený záznam z kolekce s přeloženými konstantami podle zvoleného klíče
     *
     * @param key {@link Key}
     * @param e Výčet, ze kterého se má překlad získat
     * @return Přeložený záznam
     */
    String getSingleTranslationFor(Key key, Enum e);

    /**
     * Přeloží názvy atributů podle konstant
     *
     * @param tooltipMap Mapa obsahující hodnoty pro tooltip
     */
    void translateTooltipKeys(Map<String, String> tooltipMap);

    /**
     * Obecná metoda pro vytvoření konvertoru
     *
     * @param key {@link Key} Klíč, pod kterým se nachází překlad
     * @param <T> Konkrétní datový typ výčtu
     * @return {@link StringConverter <T>}
     */
    <T> StringConverter<T> getConvertor(Key key);

    /**
     * Přeloží obyčejný záznam
     *
     * @param key Klíč, pod kterým se záznam skrývá
     * @return Přeložený záznam
     */
    String translate(String key);
}
