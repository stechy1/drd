package cz.stechy.drd.db;

import static cz.stechy.drd.db.BaseOfflineTable.ID_FILTER;

import com.google.inject.Inject;
import cz.stechy.drd.ThreadPool;
import cz.stechy.drd.db.base.OnlineRecord;
import cz.stechy.drd.model.DiffEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vylepšený databázový manažer o možnost spojení s firebase
 * Nesmí být singleton!
 * S novým pouitím se vytvoří nová instance
 */
public abstract class OfflineOnlineTableWrapper<T extends OnlineRecord> implements cz.stechy.drd.db.base.OfflineOnlineTableWrapper<T> {

    // region Constants

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineOnlineTableWrapper.class);

    // endregion

    // region Variables

    private final ObservableList<T> usedRecords = FXCollections.observableArrayList();

    private final BaseOfflineTable<T> offlineTable;
    private final BaseOnlineTable<T> onlineTable;

    private boolean showOnline = false;

    // endregion

    // region Constructors

    @Inject
    public OfflineOnlineTableWrapper(BaseOfflineTable<T> offlineTable, BaseOnlineTable<T> onlineTable) {
        this.offlineTable = offlineTable;
        this.onlineTable = onlineTable;

        attachOfflineListener();
    }

    // region Public methods

    // region Offline table delegation

    @Override
    public CompletableFuture<Void> createTableAsync() {
        return offlineTable.createTableAsync();
    }

    @Override
    public CompletableFuture<ObservableList<T>> selectAllAsync(Object... params) {
        return offlineTable.selectAllAsync(params);
    }

    @Override
    public CompletableFuture<T> selectAsync(Predicate<? super T> filter) {
        return offlineTable.selectAsync(filter);
    }

    @Override
    public CompletableFuture<ObservableList<T>> selectAllAsync() {
        return offlineTable.selectAllAsync();
    }

    @Override
    public CompletableFuture<T> insertAsync(T item) {
        return offlineTable.insertAsync(item);
    }

    @Override
    public CompletableFuture<T> updateAsync(T item) {
        return offlineTable.updateAsync(item);
    }

    @Override
    public CompletableFuture<T> deleteAsync(T item) {
        return offlineTable.deleteAsync(item);
    }

    @Override
    public void onUpgrade(int newVersion) {
        offlineTable.onUpgrade(newVersion);
    }

    @Override
    public CompletableFuture<Void> onUpgradeAsync(int newVersion) {
        return offlineTable.onUpgradeAsync(newVersion);
    }

    // endregion

    // region Online table delegation

    @Override
    public String getFirebaseChildName() {
        return onlineTable.getFirebaseChildName();
    }

    @Override
    public T fromStringMap(Map<String, Object> map) {
        return onlineTable.fromStringMap(map);
    }

    @Override
    public ObservableList<T> selectAllOnline() {
        return onlineTable.selectAllOnline();
    }

    @Override
    public CompletableFuture<T> selectOnline(Predicate<? super T> filter) {
        return onlineTable.selectOnline(filter);
    }

    @Override
    public Map<String, Object> toStringItemMap(T item) {
        return onlineTable.toStringItemMap(item);
    }

    @Override
    public CompletableFuture<Void> uploadAsync(T item) {
        return onlineTable.uploadAsync(item);
    }

    @Override
    public CompletableFuture<Void> updateOnlineAsync(T item) {
        return onlineTable.updateOnlineAsync(item);
    }

    @Override
    public CompletableFuture<Void> deleteRemoteAsync(T item) {
        return onlineTable.deleteRemoteAsync(item);
    }

    // endregion

    // endregion

    // endregion

    // region Private methods

    private void attachOfflineListener() {
        this.offlineTable.records.addListener(listChangeListener);
        this.onlineTable.records.removeListener(listChangeListener);
        this.usedRecords.setAll(this.offlineTable.records);
    }

    private void attachOnlineListener() {
        this.offlineTable.records.removeListener(listChangeListener);
        this.onlineTable.records.addListener(listChangeListener);
        this.usedRecords.setAll(this.onlineTable.records);
    }

    // Listener reagující na změnu ve zdrojovém listu a propagujíce změnu do výstupného listu
    private final ListChangeListener<? super T> listChangeListener = (ListChangeListener<T>) c -> {
        while (c.next()) {
            this.usedRecords.addAll(c.getAddedSubList());
            this.usedRecords.removeAll(c.getRemoved());
        }
    };

    // endregion

    // region Public methods

    @Override
    public ObservableList<T> getUsed() {
        return FXCollections.unmodifiableObservableList(usedRecords);
    }

    @Override
    public void toggleDatabase(boolean showOnline) {
        if (this.showOnline == showOnline) {
            return;
        }

        this.showOnline = showOnline;
        this.usedRecords.clear();

        if (showOnline) {
            attachOnlineListener();
        } else {
            attachOfflineListener();
        }
    }

    @Override
    public CompletableFuture<Integer> synchronize(final String author) {
        final List<T> filteredList = offlineTable.records.parallelStream()
            .filter(item -> item.getAuthor().equals(author))
            .collect(Collectors.toList());
        return saveAll(filteredList);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<Integer> saveAll(Collection records) {
        final List<T> workingList = new ArrayList<>(records);
        workingList.removeAll(offlineTable.records);

        if (workingList.isEmpty()) {
            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.supplyAsync(() -> {
            for (T entry : workingList) {
                entry.setDownloaded(true);
                T duplicated = entry.duplicate();
                insertAsync(duplicated).join();
            }

            return workingList.size();
        }, ThreadPool.COMMON_EXECUTOR);
    }

    @Override
    public CompletableFuture<Set<DiffEntry<T>>> getDiff() {
        return CompletableFuture.supplyAsync(() -> {
            final Set<DiffEntry<T>> diff = new HashSet<>();
            for (T record : offlineTable.records) {
                final String itemId = record.getId();
                selectOnline(ID_FILTER(itemId)).thenAccept(onlineItem -> {
                    final DiffEntry<T> diffEntry = new DiffEntry<>(record, onlineItem);
                    if (diffEntry.hasDifferentValues()) {
                        diff.add(diffEntry);
                    }
                });
            }

            return diff;
        }, ThreadPool.COMMON_EXECUTOR)
            .thenApplyAsync(set -> set, ThreadPool.JAVAFX_EXECUTOR);
    }

    @Override
    public void clearCache() {
        offlineTable.clearCache();
    }

    // endregion
}
