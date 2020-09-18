
// Works
/*
def works[B, A[_], C](f: B => [Z] => A[Z] => B, b: B, a: A[C]): B = {
  val partial = f(b)  // PolyFunction{apply: [Z](x$1: A[Z]): B}
  partial(a)
}
*/

/*
 * Perhaps this is broken because there is no type inference for polymorphic function types?
 *   https://github.com/lampepfl/dotty/issues/7594
 *
 * The polymorphic function type implementation was merged from:
 *   https://github.com/lampepfl/dotty/pull/4672
 *
 * and depending on the syntax for type lambdas changing:
 *   https://github.com/lampepfl/dotty/pull/6558
 */

// Broken
def error[B, A[_], C](f: B => [Z] => A[Z] => B, b: B, a: A[C]): B =
  f(b)(a)
