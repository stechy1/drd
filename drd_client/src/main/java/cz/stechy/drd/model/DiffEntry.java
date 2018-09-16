package cz.stechy.drd.model;

import cz.stechy.drd.annotation.TranslateEntry;
import cz.stechy.drd.db.base.DatabaseItem;
import cz.stechy.drd.util.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiffEntry<T extends DatabaseItem> {

    // region Variables

    private final String id;
    private final T left;
    private final T right;

    private final Map<String, DiffEntryTuple> diffMap;

    // endregion

    // region Constructors

    public DiffEntry(T left, T right) {
        if (!Objects.equals(left.getId(), right.getId())) {
            throw new IllegalArgumentException("Musí se porovnávat dva stejné předměty.");
        }
        id = left.getId();
        this.left = left;
        this.right = right;
        this.diffMap = calculateDifferentValues();
    }

    // endregion

    // region Private methods

    private static Optional<Method> findMethod(Method[] methods, String property) {
        property = StringUtils.capitalizeFirst(property);

        for (Method method : methods) {
            final String methodName = method.getName();
            if (methodName.contains("is" + property) || methodName.contains("get" + property) || methodName.contains("can" + property)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    private static Optional<Field> findFieldByName(List<Field> fields, String name) {
        return fields.stream().filter(field -> field.getName().equals(name)).findFirst();
    }

    /**
     * Vytvoří Mapu všech vlastností a jejich hodnot, ve kterých se instance liší
     *
     * @return {@link Map<String, DiffEntryTuple>}
     **/
    private Map<String, DiffEntryTuple> calculateDifferentValues() {
        final List<String> diffList = left.getDiffList(right);
        if (diffList.isEmpty()) {
            return Collections.emptyMap();
        }

        final Method[] methods = left.getClass().getMethods();
        final List<Field> fields = getAllFields(new LinkedList<>(), left.getClass());

        return diffList.stream()
            .map(property -> findMethod(methods, property))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(method -> {
                try {
                    final Object leftValue = method.invoke(left);
                    final Object rightValue = method.invoke(right);
                    final String methodName = method.getName();
                    final String propertyName = StringUtils.lowerFirst(methodName.replaceAll("is|get|can", ""));
                    String translationKey = propertyName;
                    final Optional<Field> optionalField = findFieldByName(fields, propertyName);
                    if (optionalField.isPresent()) {
                        final Field field = optionalField.get();
                        final TranslateEntry translateAnnotaion = field.getAnnotation(TranslateEntry.class);
                        translationKey = translateAnnotaion.key();
                    }

                    return new HashMap.SimpleImmutableEntry<>(translationKey, new DiffEntryTuple(leftValue, rightValue));
                } catch (IllegalAccessException | InvocationTargetException ignored) {}

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue));
    }

    // endregion

    // region Public methods

    public boolean hasDifferentValues() {
        return !diffMap.isEmpty();
    }

    public Map<String, DiffEntryTuple> getDiffMap() {
        return Collections.unmodifiableMap(diffMap);
    }

    // endregion

    // region Getters & Seters

    public String getId() {
        return id;
    }

    // endregion

    public static class DiffEntryTuple {
        public final Object leftValue;
        public final Object rightValue;

        private DiffEntryTuple(Object leftValue, Object rightValue) {
            this.leftValue = leftValue;
            this.rightValue = rightValue;
        }

        @Override
        public String toString() {
            return "Left: " + leftValue + " right: " + rightValue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiffEntry<?> diffEntry = (DiffEntry<?>) o;
        return Objects.equals(left, diffEntry.left) &&
            Objects.equals(right, diffEntry.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "ItemID: " + left.getId() + " with diff:" + getDiffMap().toString();
    }
}
