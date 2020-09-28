import scala.quoted.{Expr, Type, QuoteContext}

object Foo {
  inline def foo[T <: AnyKind]: String = ${ bar[T] }

  def bar[T <: AnyKind : Type](using qctx: QuoteContext): Expr[String] = {
    given as qctx.type = qctx
    import qctx.tasty.{Type => TType, given _, _}

    def packageToName(tree: Tree): Unit = tree match {
      case PackageDef(_, owner) =>
        packageToName(owner)
    }

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

    ???
  }
}
