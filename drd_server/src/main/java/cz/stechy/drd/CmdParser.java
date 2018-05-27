package cz.stechy.drd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Třída starající se o zpracování parametrů z příkazové řádky
 */
public class CmdParser {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdParser.class);

    // Konstanta parametru definujícího maximální počet klientů, kterí se můžou připojit k serveru
    public static final String CLIENTS_COUNT = "clients";
    // Konstanta parametru definujícího cestu k souboru pro fb credentials
    public static final String FB_CREDENTIALS_PATH = "credentials";
    // Konstanta parametru definujícího url adresu k firebase
    public static final String FB_URL = "fb_url";
    // Konstanta parametru definujícího maximální počet klientů čekajících ve frontě na spojení
    public static final String MAX_WAITING_QUEUE = "max_waiting_queue";
    public static final String PORT = "port";

    private final Map<String, String> map = new HashMap<>();

    /**
     * Zpracuje parametry příkazové řádky
     *
     * @param args Parametry příkazové řádky
     * @return Mapu parametrů a jejich hodnot
     */
    public CmdParser parse(String[] args) {
        for (String arg : args) {
            arg = arg.replace("-", "");
            String[] raw = arg.split("=");
            LOGGER.info(String.format("Čtu parametr: %s", Arrays.toString(raw)));
            map.put(raw[0], raw[1]);
        }

        return this;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String def) {
        final String s = map.get(key);
        return s == null ? def : s;
    }

    public int getInteger(String key) {
        return getInteger(key, -1);
    }

    public int getInteger(String key, int def) {
        final String s = map.get(key);
        return s == null ? def : Integer.parseInt(s);
    }
}
