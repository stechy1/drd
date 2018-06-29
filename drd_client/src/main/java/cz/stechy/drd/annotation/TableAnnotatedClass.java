package cz.stechy.drd.annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Přepravka obsahující informace o tabulce pro manažera
 */
final class TableAnnotatedClass {

    private final String qualifiedSuperClassName;
    private final String simpleTypeName;
    private final String tableName;
    private final String managerName;
    private final String genericClassName;
    private final boolean useFirebase;

    private String firebaseChildName;

    public TableAnnotatedClass(TypeElement classElement) {
        TypeElement annotatedClassElement = classElement;

        // Zpracování annotace Table
        Table annotation = classElement.getAnnotation(Table.class);
        this.tableName = annotation.name();
        if (tableName.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("id() in @%s for class %s is null or empty! that's not allowed",
                    Table.class.getSimpleName(), classElement.getQualifiedName().toString()));
        }
        genericClassName = classElement.getSimpleName().toString();
        managerName = genericClassName + "Manager";

        FirebaseTable firebaseAnnotation = classElement.getAnnotation(FirebaseTable.class);
        useFirebase = firebaseAnnotation != null;
        if (useFirebase) {
            firebaseChildName = firebaseAnnotation.childPath();
        }

        // Získání názvu abstraktního manažeru
        String qualifiedSuperClassName = "";
        String simpleTypeName = "";
        try {
            Class<?> clazz = annotation.parent();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            simpleTypeName = classTypeElement.getSimpleName().toString();
        } finally {
            this.qualifiedSuperClassName = qualifiedSuperClassName;
            this.simpleTypeName = simpleTypeName;
        }
    }

    public String getQualifiedSuperClassName() {
        return qualifiedSuperClassName;
    }

    public String getSimpleTypeName() {
        return simpleTypeName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getGenericClassName() {
        return genericClassName;
    }

    public boolean isUseFirebase() {
        return useFirebase;
    }

    public String getFirebaseChildName() {
        return firebaseChildName;
    }
}
