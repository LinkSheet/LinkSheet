package fe.linksheet.extension.androidx

import androidx.navigation.NavHostController

fun NavHostController.navigate(vararg routes: String) {
    for (route in routes) navigate(route)
}
