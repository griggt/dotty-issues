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

  private[this] def recordExpressions(runtime: Term, recording: Term): List[Term] = {
    val ast = recording.showExtractors

    val resetValuesSel: Term = {
      val m = runtimeSym.method("resetValues").head
      runtime.select(m)
    }

    List(
      Apply(resetValuesSel, List()),
      recordExpression(runtime, "", ast, recording)
    )
  }

  // emit recorderRuntime.recordExpression(<source>, <tree>, instrumented)
  private[this] def recordExpression(runtime: Term, source: String, ast: String, expr: Term): Term = {
    val instrumented = recordAllValues(runtime, expr)
    val recordExpressionSel: Term = {
      val m = runtimeSym.method("recordExpression").head
      runtime.select(m)
    }
    Apply(recordExpressionSel,
      List(
        Literal(Constant(source)),
        Literal(Constant(ast)),
        instrumented
      ))
  }

  private def recordAllValues(runtime: Term, expr: Term): Term =
    recordValue(runtime, recordSubValues(runtime, expr), expr)
    // expr match {
      // case New(_)     => ???
      // case Literal(_) => ???
      // case Typed(r @ Repeated(xs, y), tpe) => ??? //recordSubValues(runtime, r)
      //case _ => recordValue(runtime, recordSubValues(runtime, expr), expr)
    // }

  private def recordSubValues(runtime: Term, expr: Term): Term =
    expr match {
      case Apply(x, ys)     => Apply(recordAllValues(runtime, x), ys.map(recordAllValues(runtime, _)))
      //case TypeApply(x, ys) => ??? //TypeApply.copy(expr)(recordSubValues(runtime, x), ys)
      //case Select(x, y)     => ??? //Select.copy(expr)(recordAllValues(runtime, x), y)
      //case Typed(x, tpe)    => ??? //Typed.copy(expr)(recordSubValues(runtime, x), tpe)
      //case Repeated(xs, y)  => ??? //Repeated.copy(expr)(xs.map(recordAllValues(runtime, _)), y)
      case _                => expr
    }

  private def recordValue(runtime: Term, expr: Term, origExpr: Term): Term = {
    val recordValueSel: Term = {
      val m = runtimeSym.method("recordValue").head
      runtime.select(m)
    }

    // TEG overriding skipIdent to `true` prevents the crash
    /*
    def skipIdent(sym: Symbol): Boolean =
      sym.fullName match {
        case "scala" | "java" => true
        case fullName if fullName.startsWith("scala.") => true
        case fullName if fullName.startsWith("java.")  => true
        case _ => false
      }
    */

    expr match {
      //case Select(_, _) => ??? //expr
      //case TypeApply(_, _) => ??? //expr
      //case Ident(_) if skipIdent(expr.symbol) => expr
      case _ => // TG return just `expr` here prevents the crash
        val tapply = recordValueSel.appliedToType(expr.tpe)
        Apply.copy(expr)(tapply, List(expr,Literal(Constant(0))))
    }
  }

  //private[this] def getSourceCode(expr: Tree): String = ""
}

object RecorderMacro {
  def apply[A: Type, R: Type](recording: Expr[A], listener: Expr[RecorderListener[A, R]])(using qctx: QuoteContext): Expr[R] =
    new RecorderMacro().apply(recording, '{""}, listener)
}

object StringRecorderMacro {}
