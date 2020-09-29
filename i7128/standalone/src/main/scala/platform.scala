import java.util.concurrent.ForkJoinPool
import scala.concurrent.ExecutionContext


import scala.annotation.StaticAnnotation

// This is a dummy annotation created to match
// https://github.com/portable-scala/portable-scala-reflect/blob/v0.1.0/jvm/src/main/java/org/portablescala/reflect/annotation/EnableReflectiveInstantiation.java
// but this is acutally not used.
object internal {
    final class EnableReflectiveInstantiation extends StaticAnnotation
}

object platform {
  val Await = scala.concurrent.Await
  lazy val defaultExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool)

  type EnableReflectiveInstantiation = internal.EnableReflectiveInstantiation

  def loadModule(name: String, loader: ClassLoader): Any = {
    Reflect.loadModule(name, loader)
  }
}
