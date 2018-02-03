package cz.stechy.drd.model.db.base;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncDatabase {

    <T> CompletableFuture<List<T>> selectAsync(RowTransformHandler<T> handler, String query, Object... params);

    CompletableFuture<Long> queryAsync(String query, Object... params);

}
