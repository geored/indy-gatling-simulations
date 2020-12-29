
package io.swagger.client.model


case class PropertyChange (
    _name: Option[String],
    _originalValue: Option[Any],
    _value: Option[Any],
    _changed: Option[Boolean]
)
object PropertyChange {
    def toStringBody(var_name: Object, var_originalValue: Object, var_value: Object, var_changed: Object) =
        s"""
        | {
        | "name":$var_name,"originalValue":$var_originalValue,"value":$var_value,"changed":$var_changed
        | }
        """.stripMargin
}
