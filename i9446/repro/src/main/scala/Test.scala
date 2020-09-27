import shower.Show

object Test {
  class T
  object T {
    given Show[T] {
      def show(x: T) = ""
    }
  }
}
