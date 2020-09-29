trait SourceValue[T]{
  def value: T
}

trait SourceCompanion[T, V <: SourceValue[T]](build: T => V){
  def apply()(using V): T = summon[V].value
}

case class Name(value: String) extends SourceValue[String]
object Name extends NameMacros with SourceCompanion[String, Name](new Name(_)) {
  case class Machine(value: String) extends SourceValue[String]
  object Machine extends NameMachineMacros with SourceCompanion[String, Machine](new Machine(_))
}

case class FullName(value: String) extends SourceValue[String]
object FullName extends FullNameMacros with SourceCompanion[String, FullName](new FullName(_)) {
  case class Machine(value: String) extends SourceValue[String]
  object Machine extends FullNameMachineMacros with SourceCompanion[String, Machine](new Machine(_))
}

case class SourceFileName(value: String) extends SourceValue[String]
object SourceFileName extends SourceFileNameMacros with SourceCompanion[String, SourceFileName](new SourceFileName(_))

case class SourceFilePath(value: String) extends SourceValue[String]
object SourceFilePath extends SourceFilePathMacros with SourceCompanion[String, SourceFilePath](new SourceFilePath(_))

case class Line(value: Int) extends SourceValue[Int]
object Line extends LineMacros with SourceCompanion[Int, Line](new Line(_))

case class SourceLocation(fileName: String, filePath: String, line: Int)
object SourceLocation {
  import scala.language.implicitConversions
  implicit def toScalaVerifySourcecodeSourceLocation(implicit n: SourceFileName, p: SourceFilePath, l: Line): SourceLocation =
    SourceLocation(n.value, p.value, l.value)

  def apply()(using SourceLocation): SourceLocation = summon[SourceLocation]
}

case class Enclosing(value: String) extends SourceValue[String]
object Enclosing extends EnclosingMacros with SourceCompanion[String, Enclosing](new Enclosing(_)) {
  case class Machine(value: String) extends SourceValue[String]
  object Machine extends EnclosingMachineMacros with SourceCompanion[String, Machine](new Machine(_))
}

case class Pkg(value: String) extends SourceValue[String]
object Pkg extends PkgMacros with SourceCompanion[String, Pkg](new Pkg(_))

case class Text[T](value: T, source: String)
object Text extends TextMacros
