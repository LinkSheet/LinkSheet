package app.linksheet.feature.shizuku.service

import app.linksheet.feature.shizuku.IShizukuUserService

data class ShizukuCommand<T>(
    val command: IShizukuUserService.() -> T,
    val resultHandler: (T) -> Unit
)

fun <T> ShizukuCommand<T>.execute(userService: IShizukuUserService) {
    val result = command(userService)
    resultHandler(result)
}
