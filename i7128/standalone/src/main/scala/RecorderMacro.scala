import scala.quoted._

class RecorderMacro(using qctx0: QuoteContext) {
  import qctx0.tasty.{ Type => _, _ }
  import util._

  private[this] val runtimeSym: Symbol = '[RecorderRuntime[_, _]].unseal.tpe.typeSymbol

  def apply[A: Type, R: Type](recording: Expr[A], message: Expr[String], listener: Expr[RecorderListener[A, R]]): Expr[R] = {
    val termArg: Term = recording.unseal.underlyingArgument

    '{
      val recorderRuntime: RecorderRuntime[A, R] = new RecorderRuntime($listener)
      recorderRuntime.recordMessage($message)
      ${
        Block(
          recordExpressions('{ recorderRuntime }.unseal, termArg),
          '{ recorderRuntime.completeRecording() }.unseal
        ).seal.cast[R]
      }
    }
  }

  private def recordExpressions(runtime: Term, recording: Term): List[Term] = {
    List(recordExpression(runtime, recording))
  }

  private def recordExpression(runtime: Term, expr: Term): Term = {
    recordAllValues(runtime, expr)
  }

  private def recordAllValues(runtime: Term, expr: Term): Term =
    recordValue(runtime, recordSubValues(runtime, expr), expr)

  private def recordSubValues(runtime: Term, expr: Term): Term =
    expr match {
      case Apply(x, ys)     => Apply(recordAllValues(runtime, x), ys.map(recordAllValues(runtime, _)))
      case _                => expr
    }

  private def recordValue(runtime: Term, expr: Term, origExpr: Term): Term = {
    val recordValueSel: Term = {
      val m = runtimeSym.method("recordValue").head
      runtime.select(m)
    }

    expr match {
      //case Ident(_) if skipIdent(expr.symbol) => expr
      case _ => // TG return just `expr` here prevents the crash
        val tapply = recordValueSel.appliedToType(expr.tpe)
        Apply.copy(expr)(tapply, List(expr,Literal(Constant(0))))
    }
  }

}

object RecorderMacro {
  def apply[A: Type, R: Type](recording: Expr[A], listener: Expr[RecorderListener[A, R]])(using qctx: QuoteContext): Expr[R] =
    new RecorderMacro().apply(recording, '{""}, listener)
}

object StringRecorderMacro {}
