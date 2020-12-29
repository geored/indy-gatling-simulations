
package io.swagger.client.model


case class TrackedContentEntryDTO (
    /* The Indy key for the repository/group this where content was stored. */
    _storeKey: Option[StoreKey],
    /* Type of content access, whether \"normal\" content API or generic HTTP proxy. */
    _accessChannel: Option[String],
    _path: Option[String],
    /* If resolved from a remote repository, this is the origin URL, otherwise empty/null. */
    _originUrl: Option[String],
    /* URL to this path on the local Indy instance. */
    _localUrl: Option[String],
    _md5: Option[String],
    _sha256: Option[String],
    _sha1: Option[String],
    _size: Option[Long],
    _timestamps: Option[List[Long]]
)
object TrackedContentEntryDTO {
    def toStringBody(var_storeKey: Object, var_accessChannel: Object, var_path: Object, var_originUrl: Object, var_localUrl: Object, var_md5: Object, var_sha256: Object, var_sha1: Object, var_size: Object, var_timestamps: Object) =
        s"""
        | {
        | "storeKey":$var_storeKey,"accessChannel":$var_accessChannel,"path":$var_path,"originUrl":$var_originUrl,"localUrl":$var_localUrl,"md5":$var_md5,"sha256":$var_sha256,"sha1":$var_sha1,"size":$var_size,"timestamps":$var_timestamps
        | }
        """.stripMargin
}
