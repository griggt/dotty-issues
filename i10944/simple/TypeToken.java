import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> {
    protected TypeToken() {}

    private TypeToken(AnnotatedType type) {}

    public static <T> TypeToken<T> get(Class<T> typ) {
        return new TypeToken<T>(annotate(typ)) {};
    }

    private static AnnotatedType annotate(Type type) {
        throw new UnsupportedOperationException();
    }
}
