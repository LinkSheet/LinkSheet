package app.linksheet.feature.engine.eval.expression

sealed interface OpCode {
    val code: String

    data object Block : OpCode {
        override val code = "{"
    }

    data object If : OpCode {
        override val code = "if"
    }

    data object Const : OpCode {
        override val code = "c"
    }
}

object OpCodes {
    const val BLOCK = "{"
    const val IF = "if"
    const val CONST = "c"
    const val AND = "&"
    const val OR = "||"
    const val NOT = "!"
    const val EQ = "=="
    const val LT = "<"
    const val LTE = "<="
    const val GT = ">"
    const val GTE = ">="
    const val STRING_EQUALS = "_s.eq"
    const val STRING_CONTAINS = "_s.co"
    const val INJECT_TOKEN = "$"
    const val REGEX = "_r"
    const val REGEX_MATCH_ENTIRE = "_r.me"
    const val COMPONENT_NAME = "_cn"
    const val COMPONENT_NAME_TO_INTENT = "cn->i"
    const val PACKAGE_TO_INTENT = "p->i"
    const val URL_ENGINE_RESULT = "=u"
    const val INTENT_ENGINE_RESULT = "=i"
    const val FORWARD_OTHER_PROFILE_RESULT = "=f"
    const val URL_TO_ANDROID_URI = "_au"
    const val URL_GET_COMPONENT = "ugc"
    const val URL_SET_COMPONENT = "usc"
    const val URL_QUERY_PARAM = "uqp"
    const val URL_STRING = "us"
    const val HAS_EXTRA = "he"
    const val GET_SOURCE_APP_EXTRA = "gsae"
    const val ADD_FLAG = "af"
    const val PUT_APP_ROLE = "par"
}
