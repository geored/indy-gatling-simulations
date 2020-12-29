
package io.swagger.client.model


case class NotFoundCacheInfo (
    /* Cache size */
    _size: Long
)
object NotFoundCacheInfo {
    def toStringBody(var_size: Object) =
        s"""
        | {
        | "size":$var_size
        | }
        """.stripMargin
}
