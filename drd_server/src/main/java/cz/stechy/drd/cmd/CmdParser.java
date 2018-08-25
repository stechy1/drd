package cz.stechy.drd.cmd;

import java.util.HashMap;
import java.util.Map;

public class CmdParser implements IParameterProvider {

    // Port, na kterém bude server naslouchat
    public static final String PORT = "port";
    // Konstanta parametru definujícího maximální počet klientů, kterí se můžou připojit k serveru
    public static final String CLIENTS = "clients";
    // Konstanta parametru definujícího maximální počet klientů čekajících ve frontě na spojení
    public static final String MAX_WAITING_QUEUE = "max_waiting_queue";
    // Konstanta parametru definujícího url adresu k firebase
    public static final String FB_URL = "fb_url";

    private final Map<String, String> map = new HashMap<>();

    public CmdParser(String[] args) {
        for (String arg : args) {
            arg = arg.replace("-", "");
            String[] raw = arg.split("=");
            map.put(raw[0], raw[1]);
        }
    }

    @Override
    public String getString(String key, String def) {
        final String s = map.get(key);
        return s == null ? def : s;
    }

    @Override
    public int getInteger(String key, int def) {
        final String s = map.get(key);
        return s == null ? def : Integer.parseInt(s);
    }
}
