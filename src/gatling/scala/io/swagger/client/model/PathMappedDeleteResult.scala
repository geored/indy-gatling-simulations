
package io.swagger.client.model


case class PathMappedDeleteResult (
    _packageType: Option[String],
    _type: Option[String],
    _name: Option[String],
    _path: Option[String],
    _result: Option[Boolean]
)
object PathMappedDeleteResult {
    def toStringBody(var_packageType: Object, var_type: Object, var_name: Object, var_path: Object, var_result: Object) =
        s"""
        | {
        | "packageType":$var_packageType,"type":$var_type,"name":$var_name,"path":$var_path,"result":$var_result
        | }
        """.stripMargin
}
