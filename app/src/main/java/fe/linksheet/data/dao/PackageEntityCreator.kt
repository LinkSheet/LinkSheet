package fe.linksheet.data.dao

interface PackageEntityCreator<T> {
    fun createInstance(packageName: String): T
}