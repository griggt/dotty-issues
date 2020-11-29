// Verify.scala
import scala.quoted._

class PowerAssert:
  inline def apply(value: Boolean): Unit = ${ Macro.apply('value) }

object PowerAssert:
  def assert: PowerAssert = ???

class Runtime:
  def recordValue[U](value: U): U = ???

class Macro(using Quotes):
  import quotes.reflect._

  def apply[A: Type](x: Expr[A]): Expr[Unit] =
    val termArg: Term = Term.of(x).underlyingArgument
    '{
      val runtime: Runtime = ???
      ${
        Block(
          recordExpressions(Term.of('{ runtime }), termArg),
          Term.of('{ () })
        ).asExprOf[Unit]
      }
    }

  private def recordExpressions(runtime: Term, recording: Term): List[Term] =
    recordExpression(runtime, recording) :: Nil

  private def recordExpression(runtime: Term, expr: Term): Term =
    recordAllValues(runtime, expr)

  private def recordAllValues(runtime: Term, expr: Term): Term =
    expr match
      case New(_)     => expr
      case Literal(_) => expr
      case Select(x@This(_), y) if expr.pos.start == x.pos.start => expr
      case Typed(r @ Repeated(xs, y), tpe) => Typed.copy(r)(recordSubValues(runtime, r), tpe)
      case _ => recordValue(runtime, recordSubValues(runtime, expr), expr)

  private def recordSubValues(runtime: Term, expr: Term): Term =
    expr match
      case Apply(x, ys) =>
        try Apply(recordAllValues(runtime, x), ys.map(recordAllValues(runtime, _)))
        catch case e: AssertionError => expr
      case TypeApply(x, ys) => TypeApply.copy(expr)(recordSubValues(runtime, x), ys)
      case Select(x, y)     => Select.copy(expr)(recordAllValues(runtime, x), y)
      case Typed(x, tpe)    => Typed.copy(expr)(recordSubValues(runtime, x), tpe)
      case Repeated(xs, y)  => Repeated.copy(expr)(xs.map(recordAllValues(runtime, _)), y)
      case _                => expr

  private val runtimeSym: Symbol = TypeRepr.of[Runtime].typeSymbol

  private def recordValue(runtime: Term, expr: Term, origExpr: Term): Term =
    val sel: Term = runtime.select(runtimeSym.method("recordValue").head)

    def skipIdent(sym: Symbol): Boolean = false
    def skipSelect(sym: Symbol): Boolean = true

    expr match
      case TypeApply(_, _) => expr
      case Ident(_) if skipIdent(expr.symbol) => expr
      case Select(_, _) if skipSelect(expr.symbol) => expr
      case _ =>
        val tapply = sel.appliedToType(expr.tpe)
        Apply.copy(expr)(tapply, List(expr))
end Macro

object Macro:
  def apply[A: Type](x: Expr[A])(using Quotes): Expr[Unit] =
    new Macro().apply(x)
