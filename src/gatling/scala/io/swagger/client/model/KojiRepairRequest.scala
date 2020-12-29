
package io.swagger.client.model


case class KojiRepairRequest (
    /* Koji repository key to repair (formatted as: '{maven}:{remote,group}:name') */
    _source: StoreKey,
    /* Repair arguments */
    _args: Option[String],
    /* Get repair report ONLY, not modify anything. */
    _dryRun: Option[Boolean]
)
object KojiRepairRequest {
    def toStringBody(var_source: Object, var_args: Object, var_dryRun: Object) =
        s"""
        | {
        | "source":$var_source,"args":$var_args,"dryRun":$var_dryRun
        | }
        """.stripMargin
}
