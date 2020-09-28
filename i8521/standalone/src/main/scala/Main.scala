import scala.quoted.{Expr, Type, QuoteContext}

object BoomInspect {
  inline def inspect[T <: AnyKind]: String = ${ goAny[T] }

  def goAny[T <: AnyKind : Type](using qctx0: QuoteContext): Expr[String] = {
    new BoomInspector { val qctx = qctx0 }.doBoom[T]
    ???
  }
}

abstract class BoomInspector {
  val qctx: QuoteContext
  given as qctx.type = qctx
  import qctx.tasty.{Type => TType, given _, _}

  def doBoom[T <: AnyKind : Type]: Unit = {
    val symbol = implicitly[Type[T]].unseal.symbol

    if (!symbol.isNoSymbol) {
      symbol.tree match {
        case c: ClassDef =>
          if (!symbol.maybeOwner.isNoSymbol) {
            symbol.maybeOwner.tree match {
              case _: PackageDef =>
                packageToName(symbol.maybeOwner.tree)
            }
          }
      }
    }
  }

  private def packageToName(tree: Tree): Unit = tree match {
    case PackageDef(_, owner) =>
      packageToName(owner)
  }

}
