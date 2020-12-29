
package io.swagger.client.model


case class GroupPromoteRequest (
    /* Asynchronous call. A callback url is needed when it is true. */
    _async: Option[Boolean],
    /* Optional promotion Id */
    _promotionId: Option[String],
    /* Callback which is used to send the promotion result. */
    _callback: Option[CallbackTarget],
    /* Indy store/repository key to promote FROM (formatted as: '{maven, npm}:{remote,hosted,group}:name') */
    _source: StoreKey,
    /* Name of the Indy target group to promote TO (MUST be pre-existing) */
    _targetGroup: Option[String],
    /* Indy store/repository key to promote TO (formatted as: '{maven, npm}:{hosted,group}:name') */
    _target: Option[StoreKey],
    /* Run validations, verify source and target locations ONLY, do not modify anything! */
    _dryRun: Option[Boolean],
    _fireEvents: Option[Boolean]
)
object GroupPromoteRequest {
    def toStringBody(var_async: Object, var_promotionId: Object, var_callback: Object, var_source: Object, var_targetGroup: Object, var_target: Object, var_dryRun: Object, var_fireEvents: Object) =
        s"""
        | {
        | "async":$var_async,"promotionId":$var_promotionId,"callback":$var_callback,"source":$var_source,"targetGroup":$var_targetGroup,"target":$var_target,"dryRun":$var_dryRun,"fireEvents":$var_fireEvents
        | }
        """.stripMargin
}
