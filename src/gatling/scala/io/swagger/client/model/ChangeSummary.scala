
package io.swagger.client.model

import java.util.Date

case class ChangeSummary (
    _user: Option[String],
    _timestamp: Option[Date],
    _summary: Option[String],
    _revisionId: Option[String]
)
object ChangeSummary {
    def toStringBody(var_user: Object, var_timestamp: Object, var_summary: Object, var_revisionId: Object) =
        s"""
        | {
        | "user":$var_user,"timestamp":$var_timestamp,"summary":$var_summary,"revisionId":$var_revisionId
        | }
        """.stripMargin
}
