
package io.swagger.client.model


case class PathMappedListResult (
    _packageType: Option[String],
    _type: Option[String],
    _name: Option[String],
    _path: Option[String],
    _size: Option[Integer],
    _list: Option[List[String]]
)
object PathMappedListResult {
    def toStringBody(var_packageType: Object, var_type: Object, var_name: Object, var_path: Object, var_size: Object, var_list: Object) =
        s"""
        | {
        | "packageType":$var_packageType,"type":$var_type,"name":$var_name,"path":$var_path,"size":$var_size,"list":$var_list
        | }
        """.stripMargin
}
