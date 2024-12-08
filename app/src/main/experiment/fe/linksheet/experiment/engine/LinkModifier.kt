package fe.linksheet.experiment.engine

interface LinkModifier {
   suspend fun modify(data: ModifyInput): ModifyOutput?
}

data class ModifyInput(val url: String)

data class ModifyOutput(val url: String)




