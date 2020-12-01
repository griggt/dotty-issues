import org.apache.avro.SchemaBuilder

object Test {
  def bldr = SchemaBuilder.builder()
  def oops = bldr.fixed("foo")
}
