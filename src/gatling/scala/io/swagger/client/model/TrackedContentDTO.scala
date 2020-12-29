
package io.swagger.client.model


case class TrackedContentDTO (
    /* Session key (specified by the user) to track this record. */
    _key: Option[TrackingKey],
    _uploads: Option[List[TrackedContentEntryDTO]],
    _downloads: Option[List[TrackedContentEntryDTO]]
)
object TrackedContentDTO {
    def toStringBody(var_key: Object, var_uploads: Object, var_downloads: Object) =
        s"""
        | {
        | "key":$var_key,"uploads":$var_uploads,"downloads":$var_downloads
        | }
        """.stripMargin
}
