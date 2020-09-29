import scala.language.experimental.macros
import scala.quoted._

class Runtime {
  def fooValue[U](u: U): U = ???
}

class RecorderMacro(using qctx: QuoteContext) {
  import qctx.tasty.{ Type => _, _ }
  import util._

  private[this] val runtimeSym: Symbol = '[Runtime].unseal.tpe.typeSymbol

  def apply[A: Type, R: Type](x: Expr[A]): Expr[R] = {
    val termArg: Term = x.unseal.underlyingArgument
    '{
      val rt: Runtime = ???
      val completed: R = ???
      ${Block(doExprs('{ rt }.unseal, termArg), '{ completed }.unseal).seal.cast[R]}
    }
  }

  private def doExprs(rt: Term, t: Term): List[Term] = doAllVals(rt, t) :: Nil
  private def doAllVals(rt: Term, t: Term): Term = doVal(rt, doSubVals(rt, t))

  private def doSubVals(rt: Term, t: Term): Term = t match {
    case Apply(x, ys) => Apply(doAllVals(rt, x), ys.map(doAllVals(rt, _)))
    case _            => t
  }

  private def doVal(rt: Term, t: Term): Term = {
    val sel: Term = rt.select(runtimeSym.method("fooValue").head)
    Apply.copy(t)(sel.appliedToType(t.tpe), t :: Nil)
  }
}

object RecorderMacro {
  def apply[A: Type, R: Type](x: Expr[A])(using QuoteContext): Expr[R] =
    new RecorderMacro().apply(x)
}

abstract class Recorder[A, R] {
  inline def apply(value: A): R = ${ RecorderMacro.apply('value) }
}

class PowerAssert extends Recorder[Boolean, Unit]

trait Assertion {
  def assert: PowerAssert = ???
}

trait BasicTestSuite extends Assertion {
  def test(name: String)(f: => Unit): Unit = ???
}