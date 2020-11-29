package verify
package asserts

import scala.quoted._

class RecorderMacro(using Quotes) {
  import quotes.reflect._
  import util._

  private[this] val runtimeSym: Symbol = TypeRepr.of[RecorderRuntime[_]].typeSymbol

  def apply[A: Type](recording: Expr[A]): Expr[Unit] = {
    val termArg: Term = Term.of(recording).underlyingArgument

    '{
      val recorderRuntime: RecorderRuntime[A] = ???
      ${
        Block(
          recordExpressions(Term.of('{ recorderRuntime }), termArg),
          Term.of('{ () })
        ).asExprOf[Unit]
      }
    }
  }

  private[this] def recordExpressions(runtime: Term, recording: Term): List[Term] = {
    recordExpression(runtime, recording) :: Nil
  }

  private[this] def recordExpression(runtime: Term, expr: Term): Term = {
    val instrumented = recordAllValues(runtime, expr)
    val recordExpressionSel: Term = {
      val m = runtimeSym.method("recordExpression").head
      runtime.select(m)
    }
    Apply(recordExpressionSel, List(instrumented))
  }

  private[this] def recordAllValues(runtime: Term, expr: Term): Term =
    expr match {
      case New(_)     => expr
      case Literal(_) => expr
      case Typed(r @ Repeated(xs, y), tpe) => Typed.copy(r)(recordSubValues(runtime, r), tpe)

      // don't record value of implicit "this" added by compiler; couldn't find a better way to detect implicit "this" than via point
      case Select(x@This(_), y) if expr.pos.start == x.pos.start => expr

      case _ => recordValue(runtime, recordSubValues(runtime, expr), expr)
    }

  private[this] def recordSubValues(runtime: Term, expr: Term): Term =
    expr match {
      case Apply(x, ys) =>
        try {
          Apply(recordAllValues(runtime, x), ys.map(recordAllValues(runtime, _)))
        } catch {
          case e: AssertionError => expr
        }
      case TypeApply(x, ys) => TypeApply.copy(expr)(recordSubValues(runtime, x), ys)
      case Select(x, y)     => Select.copy(expr)(recordAllValues(runtime, x), y)
      case Typed(x, tpe)    => Typed.copy(expr)(recordSubValues(runtime, x), tpe)
      case Repeated(xs, y)  => Repeated.copy(expr)(xs.map(recordAllValues(runtime, _)), y)
      case _                => expr
    }

  private[this] def recordValue(runtime: Term, expr: Term, origExpr: Term): Term = {
    val recordValueSel: Term = {
      val m = runtimeSym.method("recordValue").head
      runtime.select(m)
    }
    def skipIdent(sym: Symbol): Boolean =
      sym.fullName match {
        case "scala" | "java" => true
        case fullName if fullName.startsWith("scala.") => true
        case fullName if fullName.startsWith("java.")  => true
        case _ => false
      }

    def skipSelect(sym: Symbol): Boolean = {
      (sym match {
        case sym if sym.isDefDef => sym.signature.paramSigs.nonEmpty
        case sym if sym.isValDef => skipIdent(sym)
        case _ => true
      })

    }
    expr match {
      case Select(_, _) if skipSelect(expr.symbol) => expr
      case TypeApply(_, _) => expr
      case Ident(_) if skipIdent(expr.symbol) => expr
      case _ =>
        val tapply = recordValueSel.appliedToType(expr.tpe)
        Apply.copy(expr)(tapply, List(expr))
    }
  }
}

object RecorderMacro {
  def apply[A: Type](recording: Expr[A])(using Quotes): Expr[Unit] =
    new RecorderMacro().apply(recording)
}
