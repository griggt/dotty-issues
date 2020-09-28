import scala.quoted.{Expr, Type, QuoteContext}

object Foo {
  inline def foo[T <: AnyKind]: String = ${ baz[T] }

  def baz[T <: AnyKind : Type](using qctx0: QuoteContext): Expr[String] = {
    new Bar { val qctx = qctx0 }.bar[T]
    ???
  }
}

abstract class Bar {
  val qctx: QuoteContext
  given as qctx.type = qctx
  import qctx.tasty.{Type => TType, given _, _}

  def bar[T <: AnyKind : Type]: Unit = {
    val sym = implicitly[Type[T]].unseal.symbol

    if (!sym.isNoSymbol) {
      sym.tree match {
        case c: ClassDef =>
          if (!sym.maybeOwner.isNoSymbol) {
            sym.maybeOwner.tree match {
              case _: PackageDef =>
                packageToName(sym.maybeOwner.tree)
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
