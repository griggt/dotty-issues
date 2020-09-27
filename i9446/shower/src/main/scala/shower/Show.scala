package shower

trait Show[T] {
  def show(x: T): String
}
