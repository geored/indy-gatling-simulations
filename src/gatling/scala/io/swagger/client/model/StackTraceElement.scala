
package io.swagger.client.model


case class StackTraceElement (
    _methodName: Option[String],
    _fileName: Option[String],
    _lineNumber: Option[Integer],
    _className: Option[String],
    _nativeMethod: Option[Boolean]
)
object StackTraceElement {
    def toStringBody(var_methodName: Object, var_fileName: Object, var_lineNumber: Object, var_className: Object, var_nativeMethod: Object) =
        s"""
        | {
        | "methodName":$var_methodName,"fileName":$var_fileName,"lineNumber":$var_lineNumber,"className":$var_className,"nativeMethod":$var_nativeMethod
        | }
        """.stripMargin
}
