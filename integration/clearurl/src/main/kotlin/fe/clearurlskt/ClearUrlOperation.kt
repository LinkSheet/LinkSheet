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
    ) : Modification(provider, input, result)

    public class RawRule(
        provider: String,
        input: String,
        result: String,
        public val regex: Regex,
    ) : Modification(provider, input, result)

    public class Redirection(
        provider: String,
        input: String,
        result: String,
        public val regex: Regex,
    ) : Modification(provider, input, result)
}

public data class Exception(val provider: String, val url: String, val regex: Regex) : ClearUrlOperation
public data class ParseFailure(val provider: String, val url: String, val parseResult: Failure<StdUrl>) : ClearUrlOperation
