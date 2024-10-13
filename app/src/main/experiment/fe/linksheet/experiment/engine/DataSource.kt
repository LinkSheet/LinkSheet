package fe.linksheet.experiment.engine

interface DataSource {
    suspend fun compute()
}
