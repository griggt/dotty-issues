import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> {
    protected TypeToken() {}

    private TypeToken(AnnotatedType type) {}  // must be private

    public static <T> TypeToken<T> get(Class<T> typ) {
        return new TypeToken<T>(null) {};
    }
}
