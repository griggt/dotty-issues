import scala.quoted._

class Runtime {
  def recordValue[U](value: U): U = ???
}

class RecorderMacro(using qctx: QuoteContext) {
  import qctx.tasty.{ Type => _, _ }
  import util._

  private[this] val runtimeSym: Symbol = '[Runtime].unseal.tpe.typeSymbol

  def apply[A: Type, R: Type](x: Expr[A]): Expr[R] = {
    val termArg: Term = x.unseal.underlyingArgument

    '{
      val runtime: Runtime = ???
      val completed: R = ???
      ${
        Block(
          recordExpressions('{ runtime }.unseal, termArg),
          '{ completed }.unseal
        ).seal.cast[R]
      }
    }
  }

  private def recordExpressions(runtime: Term, recording: Term): List[Term] = {
    recordExpression(runtime, recording) :: Nil
  }

  private def recordExpression(runtime: Term, expr: Term): Term = {
    recordAllValues(runtime, expr)
  }

  private def recordAllValues(runtime: Term, expr: Term): Term =
    recordValue(runtime, recordSubValues(runtime, expr))

  private def recordSubValues(runtime: Term, expr: Term): Term =
    expr match {
      case Apply(x, ys) => Apply(recordAllValues(runtime, x), ys.map(recordAllValues(runtime, _)))
      case _            => expr
    }

  private def recordValue(runtime: Term, expr: Term): Term = {
    val recordValueSel: Term = {
      val m = runtimeSym.method("recordValue").head
      runtime.select(m)
    }

    expr match {
      //case Ident(_) if skipIdent(expr.symbol) => expr
      case _ => // TG return just `expr` here prevents the crash
        val tapply = recordValueSel.appliedToType(expr.tpe)
        Apply.copy(expr)(tapply, expr :: Nil)
    }
  }

}

object RecorderMacro {
  def apply[A: Type, R: Type](recording: Expr[A])(using QuoteContext): Expr[R] =
    new RecorderMacro().apply(recording)
}

object StringRecorderMacro {}
