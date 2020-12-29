
package io.swagger.client.model


case class NotFoundCacheSection (
    /* Serialized store key, of the form: '[hosted|group|remote]:name' */
    _key: String,
    /* paths that failed retrieval within this store (may be empty) */
    _paths: List[String]
)
object NotFoundCacheSection {
    def toStringBody(var_key: Object, var_paths: Object) =
        s"""
        | {
        | "key":$var_key,"paths":$var_paths
        | }
        """.stripMargin
}
