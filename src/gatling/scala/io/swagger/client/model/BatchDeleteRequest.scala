
package io.swagger.client.model


case class BatchDeleteRequest (
    _storeKey: Option[StoreKey],
    _trackingID: Option[String],
    _paths: Option[List[String]]
)
object BatchDeleteRequest {
    def toStringBody(var_storeKey: Object, var_trackingID: Object, var_paths: Object) =
        s"""
        | {
        | "storeKey":$var_storeKey,"trackingID":$var_trackingID,"paths":$var_paths
        | }
        """.stripMargin
}
