
package io.swagger.client.model


case class ExpirationSet (
    _items: Option[List[Expiration]]
)
object ExpirationSet {
    def toStringBody(var_items: Object) =
        s"""
        | {
        | "items":$var_items
        | }
        """.stripMargin
}
