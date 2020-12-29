
package io.swagger.client.model


case class CallbackTarget (
    _url: Option[String],
    _method: Option[String],
    _headers: Option[Map[String, String]]
)
object CallbackTarget {
    def toStringBody(var_url: Object, var_method: Object, var_headers: Object) =
        s"""
        | {
        | "url":$var_url,"method":$var_method,"headers":$var_headers
        | }
        """.stripMargin
}
