
package io.swagger.client.model


case class LoggerDTO (
    _name: Option[String],
    _level: Option[String],
    _appenders: Option[List[String]],
    _additive: Option[Boolean],
    _originalLevel: Option[String]
)
object LoggerDTO {
    def toStringBody(var_name: Object, var_level: Object, var_appenders: Object, var_additive: Object, var_originalLevel: Object) =
        s"""
        | {
        | "name":$var_name,"level":$var_level,"appenders":$var_appenders,"additive":$var_additive,"originalLevel":$var_originalLevel
        | }
        """.stripMargin
}
