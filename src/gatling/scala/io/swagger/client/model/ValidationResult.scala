
package io.swagger.client.model


case class ValidationResult (
    /* Whether validation succeeded */
    _valid: Boolean,
    /* Mapping of rule name to error message for any failing validations */
    _validatorErrors: Option[Map[String, String]],
    /* Name of validation rule-set applied */
    _ruleSet: Option[String]
)
object ValidationResult {
    def toStringBody(var_valid: Object, var_validatorErrors: Object, var_ruleSet: Object) =
        s"""
        | {
        | "valid":$var_valid,"validatorErrors":$var_validatorErrors,"ruleSet":$var_ruleSet
        | }
        """.stripMargin
}
