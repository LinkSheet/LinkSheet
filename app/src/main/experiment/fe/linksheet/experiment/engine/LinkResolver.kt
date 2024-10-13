package fe.linksheet.experiment.engine

interface LinkResolver {
   suspend fun resolve(data: ResolveInput): ResolveOutput?
}

data class ResolveInput(val url: String)

data class ResolveOutput(val url: String)



