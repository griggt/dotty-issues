import scala.language.experimental.macros
import scala.quoted._

class Runtime {
  def fooValue[U](u: U): U = ???
}

class Macro(given qctx: QuoteContext) {
  import qctx.tasty.{ Type => _, _, given }

  def apply[A: Type](x: Expr[A]): Expr[Unit] = {
    val termArg: Term = x.unseal.underlyingArgument
    '{
      val rt: Runtime = ???
      ${Block(doExprs('{ rt }.unseal, termArg), '{ () }.unseal).seal.cast[Unit]}
    }
  }

  private def doExprs(rt: Term, t: Term): List[Term] = doAllVals(rt, t) :: Nil

  private def doAllVals(rt: Term, t: Term): Term = doVal(rt, doSubVals(rt, t))

  private def doSubVals(rt: Term, t: Term): Term = t match {
    case Apply(x, ys) => Apply(doAllVals(rt, x), ys.map(doAllVals(rt, _)))
    case _            => t
  }

  private val runtimeSym: Symbol = '[Runtime].unseal.tpe.typeSymbol

  private def doVal(rt: Term, t: Term): Term = t match {
    case TypeApply(_, _) => t
    case _ =>
      val sel: Term = rt.select(runtimeSym.method("fooValue").head)
      Apply.copy(t)(sel.appliedToType(t.tpe), t :: Nil)
  }
}

object Macro {
  def apply[A: Type](x: Expr[A])(given QuoteContext): Expr[Unit] =
    new Macro().apply(x)
}

class Assert[A] {
  inline def apply(value: A): Unit = ${ Macro.apply('value) }
}

trait TestSuite {
  def assert: Assert[Boolean] = ???
  def test(f: => Unit): Unit = ???
}
