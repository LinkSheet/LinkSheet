package fe.linksheet.experiment.engine

interface LinkModifier {
    fun modify(data: Input): Output?
}

data class Input(val url: String)

data class Output(val url: String)




