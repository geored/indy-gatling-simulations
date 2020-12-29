
package io.swagger.client.model


case class StoreKey (
    _packageType: Option[String],
    _type: Option[String],
    _name: Option[String]
)
object StoreKey {
    def toStringBody(var_packageType: Object, var_type: Object, var_name: Object) =
        s"""
        | {
        | "packageType":$var_packageType,"type":$var_type,"name":$var_name
        | }
        """.stripMargin
}
