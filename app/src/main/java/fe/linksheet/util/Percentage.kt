package fe.linksheet.util

@JvmInline
value class Percentage(val value: Int) {
    val fraction: Double
        get() = value / 100.0

    init {
        require(value in 0..100) { "Value must be >= 0 and <= 100" }
    }
}

val Int.percent: Percentage
    get() = Percentage(this)
