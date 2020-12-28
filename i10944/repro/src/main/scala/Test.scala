import io.leangen.geantyref.TypeToken

object Test {
  def error: TypeToken[String] = new TypeToken[String]() {}
}
