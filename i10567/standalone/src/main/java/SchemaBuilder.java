package org.apache.avro;

public class SchemaBuilder {
  private SchemaBuilder() {}

  public static TypeBuilder<Schema> builder() {
    throw new UnsupportedOperationException();
  }

  public static abstract class PropBuilder<S extends PropBuilder<S>> {
    protected PropBuilder() {}

    public final S prop(String name, String val) {
      throw new UnsupportedOperationException();
    }

    public final S prop(String name, Object value) {
      throw new UnsupportedOperationException();
    }
  }

  public static abstract class NamedBuilder<S extends NamedBuilder<S>> extends PropBuilder<S> {
    protected NamedBuilder(NameContext names, String name) {
      throw new UnsupportedOperationException();
    }

    public final S doc(String doc) {
      throw new UnsupportedOperationException();
    }

    final String doc() {
      throw new UnsupportedOperationException();
    }

    final String name() {
      throw new UnsupportedOperationException();
    }

    final NameContext names() {
      throw new UnsupportedOperationException();
    }
  }

  public static abstract class NamespacedBuilder<R, S extends NamespacedBuilder<R, S>> extends NamedBuilder<S> {
    protected NamespacedBuilder(Completion<R> context, NameContext names, String name) {
      super(names, name);
    }

    final String space() {
      throw new UnsupportedOperationException();
    }

    final Completion<R> context() {
      throw new UnsupportedOperationException();
    }
  }

  public static final class FixedBuilder<R> extends NamespacedBuilder<R, FixedBuilder<R>> {
    private FixedBuilder(Completion<R> context, NameContext names, String name) {
      super(context, names, name);
    }
  }

  private static class NameContext {
    private NameContext() {}

    private NameContext namespace(String namespace) {
      throw new UnsupportedOperationException();
    }

    private Schema get(String name, String namespace) {
      throw new UnsupportedOperationException();
    }

    private Schema getFullname(String fullName) {
      throw new UnsupportedOperationException();
    }

    private void put(Schema schema) {
      throw new UnsupportedOperationException();
    }

    private String resolveName(String name, String space) {
      throw new UnsupportedOperationException();
    }
  }

  public static class BaseTypeBuilder<R> {
    private BaseTypeBuilder(Completion<R> context, NameContext names) {}

    public final R type(Schema schema) {
      throw new UnsupportedOperationException();
    }

    public final R type(String name) {
      throw new UnsupportedOperationException();
    }

    public final R type(String name, String namespace) {
      throw new UnsupportedOperationException();
    }

    public final FixedBuilder<R> fixed(String name) {
      throw new UnsupportedOperationException();
    }
  }

  public static final class TypeBuilder<R> extends BaseTypeBuilder<R> {
    private TypeBuilder(Completion<R> context, NameContext names) {
      super(context, names);
    }
  }

  private abstract static class Completion<R> {
    abstract R complete(Schema schema);
  }

}
