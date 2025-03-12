package fe.linksheet.experiment.engine.modifier

interface LinkModifier {
   suspend fun warmup()
   suspend fun modify(data: ModifyInput): ModifyOutput?
}

data class ModifyInput(val url: String)

data class ModifyOutput(val url: String)




