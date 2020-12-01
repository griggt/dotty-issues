package org.apache.avro;

public class SchemaBuilder {
  public static class Schema {}

  public static TypeBuilder<Schema> builder() {
    throw new UnsupportedOperationException();
  }

  public static abstract class PropBuilder<S extends PropBuilder<S>> {}

  public static abstract class NamedBuilder<S extends NamedBuilder<S>> extends PropBuilder<S> {}

  public static abstract class NamespacedBuilder<R, S extends NamespacedBuilder<R, S>> extends NamedBuilder<S> {}

  public static final class FixedBuilder<R> extends NamespacedBuilder<R, FixedBuilder<R>> {}

  public static class BaseTypeBuilder<R> {
    public final FixedBuilder<R> fixed(String name) {
      throw new UnsupportedOperationException();
    }
  }

  public static final class TypeBuilder<R> extends BaseTypeBuilder<R> {}
}
