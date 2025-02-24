package fe.linksheet.util


@RequiresOptIn(message = "Must only be used to temporarily provide compatibility with code that has not been refactored yet")
@Retention(AnnotationRetention.BINARY)
annotation class RefactorGlue(val reason: String)
