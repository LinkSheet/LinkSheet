package fe.linksheet.experiment.engine.modifier

interface LinkModifier<Output : ModifyResult> {
   suspend fun warmup()
   suspend fun modify(data: ModifyInput): Output?
}

interface ModifyResult {

}

data class ModifyInput(val url: String)

data class ModifyOutput(val url: String)




