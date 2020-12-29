
package io.swagger.client.model


case class ReplicationDTO (
    /* Whether to overwrite pre-existing artifact stores on the local Indy instance with definitions from the remote system */
    _overwrite: Option[Boolean],
    /* The URL to the remote Indy instance */
    _apiUrl: String,
    _proxyHost: Option[String],
    _proxyPort: Option[Integer],
    /* The list of replication actions to be performed */
    _actions: List[ReplicationAction]
)
object ReplicationDTO {
    def toStringBody(var_overwrite: Object, var_apiUrl: Object, var_proxyHost: Object, var_proxyPort: Object, var_actions: Object) =
        s"""
        | {
        | "overwrite":$var_overwrite,"apiUrl":$var_apiUrl,"proxyHost":$var_proxyHost,"proxyPort":$var_proxyPort,"actions":$var_actions
        | }
        """.stripMargin
}
