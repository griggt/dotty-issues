//package org.apache.avro;

public class SchemaBuilder {
  public static class Schema {}

  public static TypeBuilder<Schema> builder() {
    throw new UnsupportedOperationException();
  }

  public static class NamedBuilder<S extends NamedBuilder<S>> {}

  public static class NamespacedBuilder<R, S extends NamespacedBuilder<R, S>> extends NamedBuilder<S> {}

  public static class FixedBuilder<R> extends NamespacedBuilder<R, FixedBuilder<R>> {}

  public static class TypeBuilder<R> {
    public final FixedBuilder<R> fixed(String name) {
      throw new UnsupportedOperationException();
    }
  }
}
