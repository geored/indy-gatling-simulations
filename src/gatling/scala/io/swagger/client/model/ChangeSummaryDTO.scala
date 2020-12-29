
package io.swagger.client.model


case class ChangeSummaryDTO (
    _items: Option[List[ChangeSummary]]
)
object ChangeSummaryDTO {
    def toStringBody(var_items: Object) =
        s"""
        | {
        | "items":$var_items
        | }
        """.stripMargin
}
