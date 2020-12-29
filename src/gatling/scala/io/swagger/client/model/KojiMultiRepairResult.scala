
package io.swagger.client.model


case class KojiMultiRepairResult (
    /* Results for all stores where repair was attempted, including failures */
    _results: Option[List[KojiRepairResult]]
)
object KojiMultiRepairResult {
    def toStringBody(var_results: Object) =
        s"""
        | {
        | "results":$var_results
        | }
        """.stripMargin
}
