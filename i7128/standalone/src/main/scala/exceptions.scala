abstract class VerifyException(message: String, cause: Throwable) extends RuntimeException(message, cause)

final class UnexpectedException(val reason: Throwable, val location: SourceLocation)
    extends VerifyException(null, reason)

final class IgnoredException(val reason: Option[String], val location: Option[SourceLocation])
    extends VerifyException(reason.orNull, null)

final class CanceledException(val reason: Option[String], val location: Option[SourceLocation])
    extends VerifyException(reason.orNull, null)

final class InterceptException(val message: String, val location: SourceLocation) extends VerifyException(message, null)

object VerifyException {

  /**
   * Utility for pattern matching.
   */
  def unapply(ex: Throwable): Option[VerifyException] = ex match {
    case ref: VerifyException =>
      Some(ref)
    case _ =>
      None
  }
}
