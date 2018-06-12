package cz.stechy.drd.firebase;

import com.google.firebase.database.DataSnapshot;
import java.util.Map;

/**
 * Konvertor {@link DataSnapshot} na {@link Map}
 */
@FunctionalInterface
public interface FirebaseConvertor {

    /**
     * Konvertuje {@link DataSnapshot} na {@link Map}
     *
     * @param snapshot {@link DataSnapshot} Snapshot přijatý z firebase
     * @return {@link Map}
     */
    Map<String, Object> convert(DataSnapshot snapshot);
}
