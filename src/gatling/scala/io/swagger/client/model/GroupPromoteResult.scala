
package io.swagger.client.model


case class GroupPromoteResult (
    /* Result code */
    _resultCode: Option[String],
    /* Result of validation rule executions, if applicable */
    _validations: Option[ValidationResult],
    /* Error message, if promotion failed */
    _error: Option[String],
    /* Original request */
    _request: Option[GroupPromoteRequest]
)
object GroupPromoteResult {
    def toStringBody(var_resultCode: Object, var_validations: Object, var_error: Object, var_request: Object) =
        s"""
        | {
        | "resultCode":$var_resultCode,"validations":$var_validations,"error":$var_error,"request":$var_request
        | }
        """.stripMargin
}
