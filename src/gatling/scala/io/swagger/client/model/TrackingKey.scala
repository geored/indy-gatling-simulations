
package io.swagger.client.model


case class TrackingKey (
    _id: Option[String]
)
object TrackingKey {
    def toStringBody(var_id: Object) =
        s"""
        | {
        | "id":$var_id
        | }
        """.stripMargin
}
