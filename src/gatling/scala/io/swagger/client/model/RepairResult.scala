
package io.swagger.client.model


case class RepairResult (
    _storeKey: Option[StoreKey],
    _changes: Option[List[PropertyChange]],
    _ignored: Option[Boolean],
    _exception: Option[Exception],
    _changed: Option[Boolean]
)
object RepairResult {
    def toStringBody(var_storeKey: Object, var_changes: Object, var_ignored: Object, var_exception: Object, var_changed: Object) =
        s"""
        | {
        | "storeKey":$var_storeKey,"changes":$var_changes,"ignored":$var_ignored,"exception":$var_exception,"changed":$var_changed
        | }
        """.stripMargin
}
