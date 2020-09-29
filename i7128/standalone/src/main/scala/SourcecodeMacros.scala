import language.experimental.macros
import scala.quoted._
import scala.tasty._

abstract class NameMacros {
  inline given Name = ${Macros.nameImpl}
}

abstract class NameMachineMacros {
  inline given Name.Machine = ${Macros.nameMachineImpl}
}

abstract class FullNameMacros {
  inline given FullName = ${Macros.fullNameImpl}
}

abstract class FullNameMachineMacros {
  inline given FullName.Machine = ${Macros.fullNameMachineImpl}
}

abstract class SourceFilePathMacros {
  inline given SourceFilePath = ${Macros.sourceFilePathImpl}
}

abstract class SourceFileNameMacros {
  inline given SourceFileName = ${Macros.sourceFileNameImpl}
}

abstract class LineMacros {
  inline given Line = ${Macros.lineImpl}
}

abstract class EnclosingMacros {
  inline given Enclosing = ${Macros.enclosingImpl}
}

abstract class EnclosingMachineMacros {
  inline given Enclosing.Machine = ${Macros.enclosingMachineImpl}
}

abstract class PkgMacros {
  inline given Pkg = ${Macros.pkgImpl}
}

abstract class TextMacros {
  import scala.language.implicitConversions
  inline implicit def toScalaVerifySourcecodeText[T](v: T): Text[T] = ${Macros.text[T]('v)}
  inline def apply[T](v: T): Text[T] = ${Macros.text[T]('v)}
}

object Util{
  def isMacro(qctx: QuoteContext)(s: qctx.tasty.Symbol): Boolean = isMacroName(getName(qctx)(s))
  def isSynthetic(qctx: QuoteContext)(s: qctx.tasty.Symbol): Boolean = isSyntheticName(getName(qctx)(s))
  def isSyntheticName(name: String) = {
    name == "<init>" || (name.startsWith("<local ") && name.endsWith(">")) || isMacroName(name)
  }
  def isMacroName(name: String) = name.startsWith("macro")
  def getName(qctx: QuoteContext)(s: qctx.tasty.Symbol): String = {
    import qctx.tasty._
    // https://github.com/lampepfl/dotty/blob/0.20.0-RC1/library/src/scala/tasty/reflect/SymbolOps.scala
    s.name.trim
  }
  def cleanName(name0: String): String = {
    name0 match {
      case name if name.endsWith("$")    => cleanName(name.dropRight(1))
      case name if name.startsWith("_$") => cleanName(name.drop(2))
      case _ => name0
    }
  }
  def literal(qctx: QuoteContext)(value: String): Expr[String] = {
    import qctx.tasty._
    Literal(Constant(value)).seal.asInstanceOf[Expr[String]]
  }
  def literal(qctx: QuoteContext)(value: Int): Expr[Int] = {
    import qctx.tasty._
    Literal(Constant(value)).seal.asInstanceOf[Expr[Int]]
  }
}

object Macros {

  def nameImpl(using qctx: QuoteContext): Expr[Name] = {
    import qctx.tasty._
    var owner = rootContext.owner
    while(Util.isSynthetic(qctx)(owner)) {
      owner = owner.owner
    }
    val simpleName = Util.cleanName(Util.getName(qctx)(owner))
    '{ Name(${Util.literal(qctx)(simpleName)}) }
  }

  def nameMachineImpl(using qctx: QuoteContext): Expr[Name.Machine] = {
    import qctx.tasty._
    val owner = rootContext.owner
    val simpleName = Util.getName(qctx)(owner)
    '{ Name.Machine(${Util.literal(qctx)(simpleName)}) }
  }

  def fullNameImpl(using qctx: QuoteContext): Expr[FullName] = {
    import qctx.tasty._
    var owner = rootContext.owner
    while(Util.isMacro(qctx)(owner)) {
      owner = owner.owner
    }
    val fullName =
      owner.fullName.trim
        .split("\\.", -1)
        .filterNot(Util.isSyntheticName)
        .map(Util.cleanName)
        .mkString(".")
    '{ FullName(${Util.literal(qctx)(fullName)}) }
  }

  def fullNameMachineImpl(using qctx: QuoteContext): Expr[FullName.Machine] = {
    import qctx.tasty._
    val owner = rootContext.owner
    val fullName = owner.fullName.trim
    '{ FullName.Machine(${Util.literal(qctx)(fullName)}) }
  }

  def sourceFileNameImpl(using qctx: QuoteContext): Expr[SourceFileName] = {
    import qctx.tasty._
    val name = Option(rootContext.source) match {
      case Some(file) => file.getFileName.toString
      case _          => "<none>"
    }
    '{ SourceFileName(${Util.literal(qctx)(name)}) }
  }

  def sourceFilePathImpl(using qctx: QuoteContext): Expr[SourceFilePath] = {
    import qctx.tasty._
    val path = Option(rootContext.source) match {
      case Some(file) => file.toString
      case _          => "<none>"
    }
    '{ SourceFilePath(${Util.literal(qctx)(path)}) }
  }

  def lineImpl(using qctx: QuoteContext): Expr[Line] = {
    import qctx.tasty._
    val line = rootPosition.startLine + 1
    '{ Line(${Util.literal(qctx)(line)}) }
  }

  def enclosingImpl(using qctx: QuoteContext): Expr[Enclosing] = {
    val path = enclosing(qctx)(!Util.isSynthetic(qctx)(_))
    '{ Enclosing(${Util.literal(qctx)(path)}) }
  }

  def enclosingMachineImpl(using qctx: QuoteContext): Expr[Enclosing.Machine] = {
    val path = enclosing(qctx)(_ => true)
    '{ Enclosing.Machine(${Util.literal(qctx)(path)}) }
  }

  def pkgImpl(using qctx: QuoteContext): Expr[Pkg] = {
    import qctx.tasty._
    // https://github.com/lampepfl/dotty/blob/0.20.0-RC1/library/src/scala/tasty/reflect/SymbolOps.scala
    val path = enclosing(qctx)(_ match {
      case sym if sym.isPackageDef => true
      case _                       => false
    })
    '{ Pkg(${Util.literal(qctx)(path)}) }
  }

  def text[T: Type](v: Expr[T])(using qctx: QuoteContext): Expr[Text[T]] = {
    import qctx.tasty._
    '{ Text($v, ${Util.literal(qctx)(rootPosition.sourceCode)}) }
  }

  enum Chunk {
    case Pkg(name: String)
    case Obj(name: String)
    case Cls(name: String)
    case Trt(name: String)
    case Val(name: String)
    case Var(name: String)
    case Lzy(name: String)
    case Def(name: String)
  }

  def enclosing(qctx: QuoteContext)(filter: qctx.tasty.Symbol => Boolean): String = {
    import qctx.tasty._
    var current = rootContext.owner
    var path = List.empty[Chunk]

    while(current != Symbol.noSymbol && current.toString != "package <root>" && current.toString != "module class <root>"){
      if (filter(current)) {
        // https://github.com/lampepfl/dotty/blob/0.20.0-RC1/library/src/scala/tasty/reflect/SymbolOps.scala
        val chunk: String => Chunk = current match {
          case x if x.isPackageDef => Chunk.Pkg(_)
          case x if x.isClassDef && x.flags.is(Flags.ModuleClass) => Chunk.Obj(_)
          case x if x.isClassDef && x.flags.is(Flags.Trait) => Chunk.Trt(_)
          case x if x.isClassDef => Chunk.Cls(_)
          case x if x.isDefDef => Chunk.Def(_)
          case x if x.isValDef => Chunk.Val(_)
        }

        path = chunk(Util.getName(qctx)(current)) :: path
      }
      current = current.owner
    }
    val renderedPath: String = path.map{
      case Chunk.Pkg(s) => s + "."
      case Chunk.Obj(s) => Util.cleanName(s) + "."
      case Chunk.Cls(s) => s + "#"
      case Chunk.Trt(s) => s + "#"
      case Chunk.Val(s) => s + " "
      case Chunk.Var(s) => s + " "
      case Chunk.Lzy(s) => s + " "
      case Chunk.Def(s) => s + " "
    }.mkString.dropRight(1)
    renderedPath
  }

}
