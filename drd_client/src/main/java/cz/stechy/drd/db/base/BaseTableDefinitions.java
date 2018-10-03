package cz.stechy.drd.db.base;

import cz.stechy.drd.util.Base64Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseTableDefinitions<T> {

    // region Public static methods

    /**
     * Převede formát Base64 na pole bytu
     *
     * @param source Base64
     * @return Pole bytu
     */
    protected static byte[] base64ToBlob(String source) {
        return Base64Util.decode(source);
    }

    /**
     * Převede poly bytu na Base64
     *
     * @param source Pole bytu
     * @return Base64
     */
    protected static String blobToBase64(byte[] source) {
        return Base64Util.encode(source);
    }

    /**
     * Přešte blob dat ze zadaného sloupečku
     *
     * @param resultSet {@link ResultSet}
     * @param column Sloupeček, který obsahuje blob
     * @return Pole bytu
     */
    protected static byte[] readBlob(ResultSet resultSet, String column) {
        final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            final InputStream binaryStream = resultSet.getBinaryStream(column);
            final byte[] buffer = new byte[1024];
            while (binaryStream.read(buffer) > 0) {
                arrayOutputStream.write(buffer);
            }
        } catch (SQLException | IOException sqlException) {
            sqlException.printStackTrace();
        }

        return arrayOutputStream.toByteArray();
    }

    // endregion

    abstract protected String getColumnKeys();

    /**
     * Konvertuje instanci třídy {@link T} na kolekci
     *
     * @param item Instance třídy {@link T}
     * @return Kolekci obsahující data o třídě {@link T}
     */
    public Map<String, Object> toStringItemMap(T item) {
        String[] columns = getColumnKeys().split(",");
        Object[] values = toParamList(item).toArray();
        assert columns.length == values.length;
        final Map<String, Object> map = new HashMap<>(columns.length);

        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i], values[i]);
        }

        return map;
    }

    /**
     * Vygeneruje řetěžec obsahující názvy sloupců oddělené čárkou
     *
     * col1,col2,col3
     *
     * @param columns Názvy sloupců
     * @return Názvy sloupců oddělené čárkou
     */
    public static String GENERATE_COLUMN_KEYS(String[] columns) {
        return String.join(",", columns);
    }

    /**
     * Vygeneruje řetězec obsahující otazníky zastupující názvy sloupců oddělené čárkou
     *
     * ?,?,?
     *
     * @param columns Názvy sloupců
     * @return Otazníky zastupující názvy sloupců oddělené čárkou
     */
    public static String GENERATE_COLUMNS_VALUES(String[] columns) {
        return Arrays.stream(columns).map(s -> "?").collect(Collectors.joining(","));
    }

    /**
     * Vygeneruje řetězec obsahující názvy sloupců ke kterým bude přiřazena hodnota představující
     * otazník a budou oddělené čárkou
     *
     * col1=?,col2=?,col3=?
     *
     * @param columns Názvy sloupců
     * @return Názvy sloupců s otazníky: col1=?,col2=?,col3=?
     */
    public static String GENERATE_COLUMNS_UPDATE(String[] columns) {
        return Arrays.stream(columns).map(s -> s + "=?").collect(Collectors.joining(","));
    }

    /**
     * Zkonvertuje {@link ResultSet} na instanci třídy {@link T}
     *
     * @param resultSet Result set z databáze
     * @return Instanci třídy {@link T}
     * @throws SQLException Pokud se nepodaří {@link ResultSet} správně naparsovat
     */
    public abstract T parseResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Zkonvertuje instanci třídy {@link T} na objekt pro parametry
     *
     * @param item {@link T}
     * @return Objekt obsahující parametry
     */
    public abstract List<Object> toParamList(T item);

    /**
     * Konvertuje {@link Map} na instanci třídy {@link T}
     *
     * @param map Kolekce obsahující data o třídě {@link T}
     * @return Instanci třídy {@link T}
     */
    public abstract T fromStringMap(Map<String, Object> map);
}
