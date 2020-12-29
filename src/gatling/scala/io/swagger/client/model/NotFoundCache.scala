
package io.swagger.client.model


case class NotFoundCache (
    /* Set of sections each corresponding an artifact store */
    _sections: List[NotFoundCache]
)
object NotFoundCache {
    def toStringBody(var_sections: Object) =
        s"""
        | {
        | "sections":$var_sections
        | }
        """.stripMargin
}
