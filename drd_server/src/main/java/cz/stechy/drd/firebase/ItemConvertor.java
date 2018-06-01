package cz.stechy.drd.firebase;

import com.google.firebase.database.DataSnapshot;
import java.util.Map;

@FunctionalInterface
public interface ItemConvertor {
    Map<String, Object> convert(DataSnapshot snapshot);
}
