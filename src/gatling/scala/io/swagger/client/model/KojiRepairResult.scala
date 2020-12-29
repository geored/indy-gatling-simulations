
package io.swagger.client.model


case class KojiRepairResult (
    /* Original request */
    _request: Option[KojiRepairRequest],
    /* Error message if failed */
    _error: Option[String],
    /* Exception object if failed because of exception */
    _exception: Option[Exception],
    /* Result entries if succeeded */
    _results: Option[List[RepairResult]]
)
object KojiRepairResult {
    def toStringBody(var_request: Object, var_error: Object, var_exception: Object, var_results: Object) =
        s"""
        | {
        | "request":$var_request,"error":$var_error,"exception":$var_exception,"results":$var_results
        | }
        """.stripMargin
}
