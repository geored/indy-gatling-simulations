
package io.swagger.client.model


case class Exception (
    _cause: Option[Throwable],
    _stackTrace: Option[List[StackTraceElement]],
    _message: Option[String],
    _localizedMessage: Option[String],
    _suppressed: Option[List[Throwable]]
)
object Exception {
    def toStringBody(var_cause: Object, var_stackTrace: Object, var_message: Object, var_localizedMessage: Object, var_suppressed: Object) =
        s"""
        | {
        | "cause":$var_cause,"stackTrace":$var_stackTrace,"message":$var_message,"localizedMessage":$var_localizedMessage,"suppressed":$var_suppressed
        | }
        """.stripMargin
}
