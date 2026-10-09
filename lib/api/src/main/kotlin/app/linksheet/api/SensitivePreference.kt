package app.linksheet.api

@Target(AnnotationTarget.PROPERTY)
@RequiresOptIn("This preference may contain sensitive data which must not be exposed")
@Retention(AnnotationRetention.BINARY)
annotation class SensitivePreference
