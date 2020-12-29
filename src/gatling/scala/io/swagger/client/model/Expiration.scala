
package io.swagger.client.model

import java.util.Date

case class Expiration (
    _name: Option[String],
    _group: Option[String],
    _expiration: Option[Date]
)
object Expiration {
    def toStringBody(var_name: Object, var_group: Object, var_expiration: Object) =
        s"""
        | {
        | "name":$var_name,"group":$var_group,"expiration":$var_expiration
        | }
        """.stripMargin
}
