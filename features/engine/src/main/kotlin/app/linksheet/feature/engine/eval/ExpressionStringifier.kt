package app.linksheet.feature.engine.eval

import app.linksheet.feature.engine.eval.expression.*

class ExpressionStringifier() {
    fun stringify(bundle: ExpressionBundle): String {
        return stringify(bundle.expression)
    }

    private fun <T> stringify(expression: Expression<T>): String {
        return when (expression) {
            is AddFlagExpression -> {
                val flag = stringify(expression.flag)
                "addFlag($flag)"
            }
            is BlockExpression -> {
                expression.expressions.joinToString(separator = "\n") { stringify(it) }
            }
            is ComponentNameExpression -> {
                val pkg = stringify(expression.pkg)
                val cls = stringify(expression.cls)
                "new ComponentName($pkg, $cls)"
            }
            is ConstantExpression<T> -> {
                if (expression.const is String) {
                    return """"${expression.const}""""
                }

                return "${expression.const}"
            }
            is ForwardOtherProfileResultExpression -> {
                return returnString("forwardOtherProfileResult", stringify(expression.expression))
            }
            is GetSourceAppExtraExpression -> {
                "getSourceAppExtra()"
            }
            is HasExtraExpression -> {
                val extra = stringify(expression.extra)
                "hasExtra($extra)"
            }
            is IfExpression<*> -> {
                val condition = stringify(expression.condition)
                val body = stringify(expression.body)

                """if ($condition) {
                |   $body
                |}""".trimMargin()
            }
            is InjectTokenExpression<*> -> {
                when(expression.name) {
                    KnownTokens.ResultUrl.name -> $$"$RESULT_URL"
                    KnownTokens.OriginalUrl.name ->$$"$ORIGINAL_URL"
                    KnownTokens.EngineRunContext.name -> $$"$CONTEXT"
                    else -> "$" + expression.name
                }
            }
            is IntentComponentNameExpression -> {
                val action = stringify(expression.action)
                val data = stringify(expression.data)
                val componentName = stringify(expression.componentName)

                "new Intent(action=$action, componentName=$componentName, data=$data)"
            }
            is IntentEngineResultExpression -> {
                return returnString("intentResult", stringify(expression.expression))
            }
            is IntentPackageExpression -> {
                val action = stringify(expression.action)
                val data = stringify(expression.data)
                val packageName = stringify(expression.packageName)

                "new Intent(action=$action, packageName=$packageName, data=$data)"
            }
            is NotExpression -> {
                "!${stringify(expression.expression)}"
            }
            is PutAppRoleExpression -> {
                "putRole(${stringify(expression.packageName)}, ${stringify(expression.id)})"
            }
            is RegexExpression -> {
                "new Regex(${stringify(expression.expression)})"
            }
            is RegexMatchEntireExpression -> {
                val regex = stringify(expression.regex)
                val string = stringify(expression.string)
                "${regex}.matcheEntire($string)"
            }
            is StringContainsExpression -> {
                val str = stringify(expression.left)
                val contains = stringify(expression.right)
                "${str}.contains($contains, ignoreCase=${expression.ignoreCase})"
            }
            is StringEqualsExpression -> {
                val str = stringify(expression.left)
                val contains = stringify(expression.right)
                "${str}.equals($contains, ignoreCase=${expression.ignoreCase})"
            }
            is UrlEngineResultExpression -> {
                return returnString("urlEngineResult", stringify(expression.expression))
            }
            is UrlGetComponentExpression -> {
                val url = stringify(expression.expression)
                val component = stringify(expression.component)
                "$url.getComponent($component)"
            }
            is UrlQueryParamExpression -> {
                val url = stringify(expression.op)
                val param = stringify(expression.key)
                "$url.getParam($param)"
            }
            is UrlSetComponentExpression -> {
                val url = stringify(expression.expression)
                val component = stringify(expression.component)
                val value = stringify(expression.value)
                "$url.setComponent($component, $value)"
            }
            is UrlStringExpression -> {
                val url = stringify(expression.expression)
//                "$url.toString()"
                url
            }
            is UrlToAndroidUriExpression -> {
                val url = stringify(expression.expression)
//                "$url.toAndroidUri()"
                url
            }
            is LeftRightExpression<*> -> {
                val left = stringify(expression.left)
                val right = stringify(expression.right)

                when(expression) {
                    is EqualsExpression<*> -> "$left == $right"
                    is GreaterThanEqualExpression<*> -> "$left >= $right"
                    is GreaterThanExpression<*> -> "$left > $right"
                    is LessThanEqualExpression<*> -> "$left <= $right"
                    is LessThanExpression<*> -> "$left < $right"
                    is OrExpression -> "$left || $right"
                    is AndExpression -> "$left && $right"
                }
            }
        }
    }

    private fun returnString(type: String, value: String): String {
        return "return $type($value)"
    }
}
