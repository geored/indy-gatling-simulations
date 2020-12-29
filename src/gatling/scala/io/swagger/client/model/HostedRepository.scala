
package io.swagger.client.model


case class HostedRepository (
    /* Serialized store key, of the form: '[hosted|group|remote]:name' */
    _key: String,
    _description: Option[String],
    _metadata: Option[Map[String, String]],
    _disabled: Option[Boolean],
    _packageType: Option[String],
    _name: Option[String],
    _type: Option[String],
    /* Integer time in seconds which is used for repo automatically re-enable when set disable by errors, positive value means time in seconds, -1 means never disable, empty or 0 means use default timeout. */
    _disable_timeout: Option[Integer],
    _path_style: Option[String],
    _path_mask_patterns: Option[List[String]],
    _authoritative_index: Option[Boolean],
    _create_time: Option[String],
    _storage: Option[String],
    _snapshotTimeoutSeconds: Option[Integer],
    /* identify if the hoste repo is readonly */
    _readonly: Option[Boolean],
    _allow_snapshots: Option[Boolean],
    _allow_releases: Option[Boolean]
)
object HostedRepository {
    def toStringBody(var_key: Object, var_description: Object, var_metadata: Object, var_disabled: Object, var_packageType: Object, var_name: Object, var_type: Object, var_disable_timeout: Object, var_path_style: Object, var_path_mask_patterns: Object, var_authoritative_index: Object, var_create_time: Object, var_storage: Object, var_snapshotTimeoutSeconds: Object, var_readonly: Object, var_allow_snapshots: Object, var_allow_releases: Object) =
        s"""
        | {
        | "key":$var_key,"description":$var_description,"metadata":$var_metadata,"disabled":$var_disabled,"packageType":$var_packageType,"name":$var_name,"type":$var_type,"disable_timeout":$var_disable_timeout,"path_style":$var_path_style,"path_mask_patterns":$var_path_mask_patterns,"authoritative_index":$var_authoritative_index,"create_time":$var_create_time,"storage":$var_storage,"snapshotTimeoutSeconds":$var_snapshotTimeoutSeconds,"readonly":$var_readonly,"allow_snapshots":$var_allow_snapshots,"allow_releases":$var_allow_releases
        | }
        """.stripMargin
}
