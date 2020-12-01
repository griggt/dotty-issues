package org.apache.avro;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.internal.JacksonUtils;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class SchemaBuilder {

  private SchemaBuilder() {
  }

  /**
   * Create a builder for Avro schemas.
   */
  public static TypeBuilder<Schema> builder() {
    return new TypeBuilder<>(new SchemaCompletion(), new NameContext());
  }

  /**
   * An abstract builder for all Avro types. All Avro types can have arbitrary
   * string key-value properties.
   */
  public static abstract class PropBuilder<S extends PropBuilder<S>> {
    private Map<String, JsonNode> props = null;

    protected PropBuilder() {
    }

    /**
     * Set name-value pair properties for this type or field.
     */
    public final S prop(String name, String val) {
      return prop(name, TextNode.valueOf(val));
    }

    /**
     * Set name-value pair properties for this type or field.
     */
    public final S prop(String name, Object value) {
      return prop(name, JacksonUtils.toJsonNode(value));
    }

    // for internal use by the Parser
    final S prop(String name, JsonNode val) {
      if (!hasProps()) {
        props = new HashMap<>();
      }
      props.put(name, val);
      return self();
    }

    private boolean hasProps() {
      return (props != null);
    }

    final <T extends JsonProperties> T addPropsTo(T jsonable) {
      if (hasProps()) {
        for (Map.Entry<String, JsonNode> prop : props.entrySet()) {
          jsonable.addProp(prop.getKey(), prop.getValue());
        }
      }
      return jsonable;
    }

    /**
     * a self-type for chaining builder subclasses. Concrete subclasses must return
     * 'this'
     **/
    protected abstract S self();
  }

  /**
   * An abstract type that provides builder methods for configuring the name, doc,
   * and aliases of all Avro types that have names (fields, Fixed, Record, and
   * Enum).
   * <p/>
   * All Avro named types and fields have 'doc', 'aliases', and 'name' components.
   * 'name' is required, and provided to this builder. 'doc' and 'aliases' are
   * optional.
   */
  public static abstract class NamedBuilder<S extends NamedBuilder<S>> extends PropBuilder<S> {
    private final String name;
    private final NameContext names;
    private String doc;
    private String[] aliases;

    protected NamedBuilder(NameContext names, String name) {
      this.name = Objects.requireNonNull(name, "Type must have a name");
      this.names = names;
    }

    /** configure this type's optional documentation string **/
    public final S doc(String doc) {
      this.doc = doc;
      return self();
    }

    /** configure this type's optional name aliases **/
    public final S aliases(String... aliases) {
      this.aliases = aliases;
      return self();
    }

    final String doc() {
      return doc;
    }

    final String name() {
      return name;
    }

    final NameContext names() {
      return names;
    }

    final Schema addAliasesTo(Schema schema) {
      if (null != aliases) {
        for (String alias : aliases) {
          schema.addAlias(alias);
        }
      }
      return schema;
    }

    final Field addAliasesTo(Field field) {
      if (null != aliases) {
        for (String alias : aliases) {
          field.addAlias(alias);
        }
      }
      return field;
    }
  }

  /**
   * An abstract type that provides builder methods for configuring the namespace
   * for all Avro types that have namespaces (Fixed, Record, and Enum).
   */
  public static abstract class NamespacedBuilder<R, S extends NamespacedBuilder<R, S>> extends NamedBuilder<S> {
    private final Completion<R> context;
    private String namespace;

    protected NamespacedBuilder(Completion<R> context, NameContext names, String name) {
      super(names, name);
      this.context = context;
    }

    /**
     * Set the namespace of this type. To clear the namespace, set empty string.
     * <p/>
     * When the namespace is null or unset, the namespace of the type defaults to
     * the namespace of the enclosing context.
     **/
    public final S namespace(String namespace) {
      this.namespace = namespace;
      return self();
    }

    final String space() {
      if (null == namespace) {
        return names().namespace;
      }
      return namespace;
    }

    final Schema completeSchema(Schema schema) {
      addPropsTo(schema);
      addAliasesTo(schema);
      names().put(schema);
      return schema;
    }

    final Completion<R> context() {
      return context;
    }
  }

  /**
   * Builds an Avro Fixed type with optional properties, namespace, doc, and
   * aliases.
   * <p/>
   * Set properties with {@link #prop(String, String)}, namespace with
   * {@link #namespace(String)}, doc with {@link #doc(String)}, and aliases with
   * {@link #aliases(String[])}.
   * <p/>
   * The Fixed schema is finalized when its required size is set via
   * {@link #size(int)}.
   **/
  public static final class FixedBuilder<R> extends NamespacedBuilder<R, FixedBuilder<R>> {
    private FixedBuilder(Completion<R> context, NameContext names, String name) {
      super(context, names, name);
    }

    private static <R> FixedBuilder<R> create(Completion<R> context, NameContext names, String name) {
      return new FixedBuilder<>(context, names, name);
    }

    @Override
    protected FixedBuilder<R> self() {
      return this;
    }

    /** Configure this fixed type's size, and end its configuration. **/
    public R size(int size) {
      Schema schema = Schema.createFixed(name(), super.doc(), space(), size);
      completeSchema(schema);
      return context().complete(schema);
    }
  }

  /**
   * internal class for passing the naming context around. This allows for the
   * following:
   * <li>Cache and re-use primitive schemas when they do not set properties.</li>
   * <li>Provide a default namespace for nested contexts (as the JSON Schema spec
   * does).</li>
   * <li>Allow previously defined named types or primitive types to be referenced
   * by name.</li>
   **/
  private static class NameContext {
    private static final Set<String> PRIMITIVES = new HashSet<>();
    static {
      PRIMITIVES.add("null");
      PRIMITIVES.add("boolean");
      PRIMITIVES.add("int");
      PRIMITIVES.add("long");
      PRIMITIVES.add("float");
      PRIMITIVES.add("double");
      PRIMITIVES.add("bytes");
      PRIMITIVES.add("string");
    }
    private final HashMap<String, Schema> schemas;
    private final String namespace;

    private NameContext() {
      this.schemas = new HashMap<>();
      this.namespace = null;
      schemas.put("null", Schema.create(Schema.Type.NULL));
      schemas.put("boolean", Schema.create(Schema.Type.BOOLEAN));
      schemas.put("int", Schema.create(Schema.Type.INT));
      schemas.put("long", Schema.create(Schema.Type.LONG));
      schemas.put("float", Schema.create(Schema.Type.FLOAT));
      schemas.put("double", Schema.create(Schema.Type.DOUBLE));
      schemas.put("bytes", Schema.create(Schema.Type.BYTES));
      schemas.put("string", Schema.create(Schema.Type.STRING));
    }

    private NameContext(HashMap<String, Schema> schemas, String namespace) {
      this.schemas = schemas;
      this.namespace = "".equals(namespace) ? null : namespace;
    }

    private NameContext namespace(String namespace) {
      return new NameContext(schemas, namespace);
    }

    private Schema get(String name, String namespace) {
      return getFullname(resolveName(name, namespace));
    }

    private Schema getFullname(String fullName) {
      Schema schema = schemas.get(fullName);
      if (schema == null) {
        throw new SchemaParseException("Undefined name: " + fullName);
      }
      return schema;
    }

    private void put(Schema schema) {
      String fullName = schema.getFullName();
      if (schemas.containsKey(fullName)) {
        throw new SchemaParseException("Can't redefine: " + fullName);
      }
      schemas.put(fullName, schema);
    }

    private String resolveName(String name, String space) {
      if (PRIMITIVES.contains(name) && space == null) {
        return name;
      }
      int lastDot = name.lastIndexOf('.');
      if (lastDot < 0) { // short name
        if (space == null) {
          space = namespace;
        }
        if (space != null && !"".equals(space)) {
          return space + "." + name;
        }
      }
      return name;
    }
  }

  /**
   * A common API for building types within a context. BaseTypeBuilder can build
   * all types other than Unions. {@link TypeBuilder} can additionally build
   * Unions.
   * <p/>
   * The builder has two contexts:
   * <li>A naming context provides a default namespace and allows for previously
   * defined named types to be referenced from {@link #type(String)}</li>
   * <li>A completion context representing the scope that the builder was created
   * in. A builder created in a nested context (for example,
   * {@link MapBuilder#values()} will have a completion context assigned by the
   * {@link MapBuilder}</li>
   **/
  public static class BaseTypeBuilder<R> {
    private final Completion<R> context;
    private final NameContext names;

    private BaseTypeBuilder(Completion<R> context, NameContext names) {
      this.context = context;
      this.names = names;
    }

    /** Use the schema provided as the type. **/
    public final R type(Schema schema) {
      return context.complete(schema);
    }

    /**
     * Look up the type by name. This type must be previously defined in the context
     * of this builder.
     * <p/>
     * The name may be fully qualified or a short name. If it is a short name, the
     * default namespace of the current context will additionally be searched.
     **/
    public final R type(String name) {
      return type(name, null);
    }

    /**
     * Look up the type by name and namespace. This type must be previously defined
     * in the context of this builder.
     * <p/>
     * The name may be fully qualified or a short name. If it is a fully qualified
     * name, the namespace provided is ignored. If it is a short name, the namespace
     * provided is used if not null, else the default namespace of the current
     * context will be used.
     **/
    public final R type(String name, String namespace) {
      return type(names.get(name, namespace));
    }

    /**
     * Build an Avro fixed type. Example usage:
     *
     * <pre>
     * fixed("com.foo.IPv4").size(4)
     * </pre>
     *
     * Equivalent to Avro JSON Schema:
     *
     * <pre>
     * {"type":"fixed", "name":"com.foo.IPv4", "size":4}
     * </pre>
     **/
    public final FixedBuilder<R> fixed(String name) {
      return FixedBuilder.create(context, names, name);
    }

    /**
     * A shortcut for building a union of a type and null.
     * <p/>
     * For example, the code snippets below are equivalent:
     *
     * <pre>
     * nullable().booleanType()
     * </pre>
     *
     * <pre>
     * unionOf().booleanType().and().nullType().endUnion()
     * </pre>
     **/
    protected BaseTypeBuilder<R> nullable() {
      return new BaseTypeBuilder<>(new NullableCompletion<>(context), names);
    }

  }

  /**
   * A Builder for creating any Avro schema type.
   **/
  public static final class TypeBuilder<R> extends BaseTypeBuilder<R> {
    private TypeBuilder(Completion<R> context, NameContext names) {
      super(context, names);
    }

    @Override
    public BaseTypeBuilder<R> nullable() {
      return super.nullable();
    }
  }

  public final static class FieldAssembler<R> {
    private final List<Field> fields = new ArrayList<>();
    private final Completion<R> context;
    private final NameContext names;
    private final Schema record;

    private FieldAssembler(Completion<R> context, NameContext names, Schema record) {
      this.context = context;
      this.names = names;
      this.record = record;
    }

    /**
     * Add a field with the given name.
     *
     * @return A {@link FieldBuilder} for the given name.
     */
    public FieldBuilder<R> name(String fieldName) {
      return new FieldBuilder<>(this, names, fieldName);
    }

    /**
     * Shortcut for creating a boolean field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().booleanType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredBoolean(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional boolean field: a union of null and boolean
     * with null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().booleanType()
     * </pre>
     */
    public FieldAssembler<R> optionalBoolean(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable boolean field: a union of boolean and null
     * with an boolean default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().booleanType().booleanDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableBoolean(String fieldName, boolean defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an int field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().intType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredInt(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional int field: a union of null and int with
     * null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().intType()
     * </pre>
     */
    public FieldAssembler<R> optionalInt(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable int field: a union of int and null with an
     * int default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().intType().intDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableInt(String fieldName, int defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a long field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().longType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredLong(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional long field: a union of null and long with
     * null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().longType()
     * </pre>
     */
    public FieldAssembler<R> optionalLong(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable long field: a union of long and null with a
     * long default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().longType().longDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableLong(String fieldName, long defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a float field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().floatType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredFloat(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional float field: a union of null and float with
     * null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().floatType()
     * </pre>
     */
    public FieldAssembler<R> optionalFloat(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable float field: a union of float and null with
     * a float default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().floatType().floatDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableFloat(String fieldName, float defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a double field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().doubleType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredDouble(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional double field: a union of null and double
     * with null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().doubleType()
     * </pre>
     */
    public FieldAssembler<R> optionalDouble(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable double field: a union of double and null
     * with a double default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().doubleType().doubleDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableDouble(String fieldName, double defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a string field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().stringType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredString(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional string field: a union of null and string
     * with null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().stringType()
     * </pre>
     */
    public FieldAssembler<R> optionalString(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable string field: a union of string and null
     * with a string default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().stringType().stringDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableString(String fieldName, String defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a bytes field with the given name and no default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().bytesType().noDefault()
     * </pre>
     */
    public FieldAssembler<R> requiredBytes(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating an optional bytes field: a union of null and bytes with
     * null default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().optional().bytesType()
     * </pre>
     */
    public FieldAssembler<R> optionalBytes(String fieldName) {
      throw new UnsupportedOperationException();
    }

    /**
     * Shortcut for creating a nullable bytes field: a union of bytes and null with
     * a bytes default.
     * <p/>
     * This is equivalent to:
     *
     * <pre>
     * name(fieldName).type().nullable().bytesType().bytesDefault(defaultVal)
     * </pre>
     */
    public FieldAssembler<R> nullableBytes(String fieldName, byte[] defaultVal) {
      throw new UnsupportedOperationException();
    }

    /**
     * End adding fields to this record, returning control to the context that this
     * record builder was created in.
     */
    public R endRecord() {
      record.setFields(fields);
      return context.complete(record);
    }

    private FieldAssembler<R> addField(Field field) {
      fields.add(field);
      return this;
    }

  }

  /**
   * Builds a Field in the context of a {@link FieldAssembler}.
   *
   * Usage is to first configure any of the optional parameters and then to call
   * one of the type methods to complete the field. For example
   *
   * <pre>
   *   .namespace("org.apache.example").orderDescending().type()
   * </pre>
   *
   * Optional parameters for a field are namespace, doc, order, and aliases.
   */
  public final static class FieldBuilder<R> extends NamedBuilder<FieldBuilder<R>> {
    private final FieldAssembler<R> fields;
    private Schema.Field.Order order = Schema.Field.Order.ASCENDING;

    private FieldBuilder(FieldAssembler<R> fields, NameContext names, String name) {
      super(names, name);
      this.fields = fields;
    }

    /** Set this field to have ascending order. Ascending is the default **/
    public FieldBuilder<R> orderAscending() {
      order = Schema.Field.Order.ASCENDING;
      return self();
    }

    /** Set this field to have descending order. Descending is the default **/
    public FieldBuilder<R> orderDescending() {
      order = Schema.Field.Order.DESCENDING;
      return self();
    }

    /** Set this field to ignore order. **/
    public FieldBuilder<R> orderIgnore() {
      order = Schema.Field.Order.IGNORE;
      return self();
    }

    /**
     * Final step in configuring this field, finalizing name, namespace, alias, and
     * order. Sets the field's type to the provided schema, returns a
     * {@link GenericDefault}.
     */
    public GenericDefault<R> type(Schema type) {
      return new GenericDefault<>(this, type);
    }

    /**
     * Final step in configuring this field, finalizing name, namespace, alias, and
     * order. Sets the field's type to the schema by name reference.
     * <p/>
     * The name must correspond with a named schema that has already been created in
     * the context of this builder. The name may be a fully qualified name, or a
     * short name. If it is a short name, the namespace context of this builder will
     * be used.
     * <p/>
     * The name and namespace context rules are the same as the Avro schema JSON
     * specification.
     */
    public GenericDefault<R> type(String name) {
      return type(name, null);
    }

    /**
     * Final step in configuring this field, finalizing name, namespace, alias, and
     * order. Sets the field's type to the schema by name reference.
     * <p/>
     * The name must correspond with a named schema that has already been created in
     * the context of this builder. The name may be a fully qualified name, or a
     * short name. If it is a full name, the namespace is ignored. If it is a short
     * name, the namespace provided is used. If the namespace provided is null, the
     * namespace context of this builder will be used.
     * <p/>
     * The name and namespace context rules are the same as the Avro schema JSON
     * specification.
     */
    public GenericDefault<R> type(String name, String namespace) {
      Schema schema = names().get(name, namespace);
      return type(schema);
    }

    private FieldAssembler<R> completeField(Schema schema, Object defaultVal) {
      JsonNode defaultNode = defaultVal == null ? NullNode.getInstance() : toJsonNode(defaultVal);
      return completeField(schema, defaultNode);
    }

    private FieldAssembler<R> completeField(Schema schema) {
      return completeField(schema, (JsonNode) null);
    }

    private FieldAssembler<R> completeField(Schema schema, JsonNode defaultVal) {
      Field field = new Field(name(), schema, doc(), defaultVal, true, order);
      addPropsTo(field);
      addAliasesTo(field);
      return fields.addField(field);
    }

    @Override
    protected FieldBuilder<R> self() {
      return this;
    }
  }

  /** Abstract base class for field defaults. **/
  public static abstract class FieldDefault<R, S extends FieldDefault<R, S>> extends Completion<S> {
    private final FieldBuilder<R> field;
    private Schema schema;

    FieldDefault(FieldBuilder<R> field) {
      this.field = field;
    }

    /** Completes this field with no default value **/
    public final FieldAssembler<R> noDefault() {
      return field.completeField(schema);
    }

    private FieldAssembler<R> usingDefault(Object defaultVal) {
      return field.completeField(schema, defaultVal);
    }

    @Override
    final S complete(Schema schema) {
      this.schema = schema;
      return self();
    }

    abstract S self();
  }

  /** Choose whether to use a default value for the field or not. **/
  public static class FixedDefault<R> extends FieldDefault<R, FixedDefault<R>> {
    private FixedDefault(FieldBuilder<R> field) {
      super(field);
    }

    /** Completes this field with the default value provided, cannot be null **/
    public final FieldAssembler<R> fixedDefault(byte[] defaultVal) {
      return super.usingDefault(ByteBuffer.wrap(defaultVal));
    }

    /** Completes this field with the default value provided, cannot be null **/
    public final FieldAssembler<R> fixedDefault(ByteBuffer defaultVal) {
      return super.usingDefault(defaultVal);
    }

    /**
     * Completes this field with the default value provided, cannot be null. The
     * string is interpreted as a byte[], with each character code point value
     * equalling the byte value, as in the Avro spec JSON default.
     **/
    public final FieldAssembler<R> fixedDefault(String defaultVal) {
      return super.usingDefault(defaultVal);
    }

    @Override
    final FixedDefault<R> self() {
      return this;
    }
  }

  public final static class GenericDefault<R> {
    private final FieldBuilder<R> field;
    private final Schema schema;

    private GenericDefault(FieldBuilder<R> field, Schema schema) {
      this.field = field;
      this.schema = schema;
    }

    /** Do not use a default value for this field. **/
    public FieldAssembler<R> noDefault() {
      return field.completeField(schema);
    }

    /**
     * Completes this field with the default value provided. The value must conform
     * to the schema of the field.
     **/
    public FieldAssembler<R> withDefault(Object defaultVal) {
      return field.completeField(schema, defaultVal);
    }
  }

  /**
   * Completion<R> is for internal builder use, all subclasses are private.
   *
   * Completion is an object that takes a Schema and returns some result.
   */
  private abstract static class Completion<R> {
    abstract R complete(Schema schema);
  }

  private static class SchemaCompletion extends Completion<Schema> {
    @Override
    protected Schema complete(Schema schema) {
      return schema;
    }
  }

  private static final Schema NULL_SCHEMA = Schema.create(Schema.Type.NULL);

  private static class NullableCompletion<R> extends Completion<R> {
    private final Completion<R> context;

    private NullableCompletion(Completion<R> context) {
      this.context = context;
    }

    @Override
    protected R complete(Schema schema) {
      // wrap the schema as a union of the schema and null
      Schema nullable = Schema.createUnion(Arrays.asList(schema, NULL_SCHEMA));
      return context.complete(nullable);
    }
  }

  private static class OptionalCompletion<R> extends Completion<FieldAssembler<R>> {
    private final FieldBuilder<R> bldr;

    public OptionalCompletion(FieldBuilder<R> bldr) {
      this.bldr = bldr;
    }

    @Override
    protected FieldAssembler<R> complete(Schema schema) {
      // wrap the schema as a union of null and the schema
      Schema optional = Schema.createUnion(Arrays.asList(NULL_SCHEMA, schema));
      return bldr.completeField(optional, (Object) null);
    }
  }

  private abstract static class CompletionWrapper {
    abstract <R> Completion<R> wrap(Completion<R> completion);
  }

  private static final class NullableCompletionWrapper extends CompletionWrapper {
    @Override
    <R> Completion<R> wrap(Completion<R> completion) {
      return new NullableCompletion<>(completion);
    }
  }

  // create default value JsonNodes from objects
  private static JsonNode toJsonNode(Object o) {
    throw new UnsupportedOperationException();
  }
}
