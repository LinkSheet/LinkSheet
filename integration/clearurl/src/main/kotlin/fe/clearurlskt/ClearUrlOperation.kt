package fe.clearurlskt

import fe.std.result.Failure
import fe.std.uri.StdUrl

public sealed interface ClearUrlOperation

public sealed class Modification(
    public val provider: String,
    public val input: String,
    public val result: String
) : ClearUrlOperation {
    public class ParameterRemoval(
        provider: String,
        input: String,
        result: String,
        public val fields: MutableSet<String>,
        public val fragment: MutableSet<String>,
    ) : Modification(provider, input, result) {
        override fun toString(): String {
            return "ParameterRemoval(provider=$provider, input=$input, result=$result, fields=$fields, fragment=$fragment)"
        }
    }

    public class RawRule(
        provider: String,
        input: String,
        result: String,
        public val regex: Regex,
    ) : Modification(provider, input, result){
        override fun toString(): String {
            return "RawRule(provider=$provider, input=$input, result=$result, regex=$regex)"
        }
    }

    public class Redirection(
        provider: String,
        input: String,
        result: String,
        public val regex: Regex,
    ) : Modification(provider, input, result) {
        override fun toString(): String {
            return "Redirection(provider=$provider, input=$input, result=$result, regex=$regex)"
        }
    }
}

public data class Exception(val provider: String, val url: String, val regex: Regex) : ClearUrlOperation
public data class ParseFailure(val provider: String, val url: String, val parseResult: Failure<StdUrl>) : ClearUrlOperation
