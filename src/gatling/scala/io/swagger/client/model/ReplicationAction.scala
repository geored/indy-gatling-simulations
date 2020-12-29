
package io.swagger.client.model


case class ReplicationAction (
    _type: Option[String],
    _include: Option[String],
    _exclude: Option[String],
    _proxyHost: Option[String],
    _proxyPort: Option[Integer],
    _proxyUser: Option[String],
    _proxyPass: Option[String]
)
object ReplicationAction {
    def toStringBody(var_type: Object, var_include: Object, var_exclude: Object, var_proxyHost: Object, var_proxyPort: Object, var_proxyUser: Object, var_proxyPass: Object) =
        s"""
        | {
        | "type":$var_type,"include":$var_include,"exclude":$var_exclude,"proxyHost":$var_proxyHost,"proxyPort":$var_proxyPort,"proxyUser":$var_proxyUser,"proxyPass":$var_proxyPass
        | }
        """.stripMargin
}
