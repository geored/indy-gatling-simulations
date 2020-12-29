
package io.swagger.client.model


case class ListOfArtifactStoreDefinitions (
    /* The store definition list */
    _items: ArtifactStore
)
object ListOfArtifactStoreDefinitions {
    def toStringBody(var_items: Object) =
        s"""
        | {
        | "items":$var_items
        | }
        """.stripMargin
}
