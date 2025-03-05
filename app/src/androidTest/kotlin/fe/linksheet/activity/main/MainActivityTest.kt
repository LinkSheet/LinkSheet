package fe.linksheet.activity.main

import android.util.Log
import androidx.collection.forEach
import androidx.collection.valueIterator
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import fe.linksheet.UnitTest
import org.junit.Rule
import kotlin.test.Test

internal class MainActivityTest : UnitTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
//        val navHostController =     NavHostController(applicationContext).apply {
////            navigatorProvider.addNavigator(ComposeNavGraphNavigator(navigatorProvider))
//            navigatorProvider.addNavigator(ComposeNavigator())
//            navigatorProvider.addNavigator(DialogNavigator())
//        }
//
//        composeTestRule.setContent {
//            MainNavHost(
//                navController = navHostController,
//                navigate = navHostController::navigate,
//                onBackPressed = navHostController::popBackStack
//            )
//        }
//
//        composeTestRule.onRoot().printToLog("currentLabelExists")


//        for(node in navHostController.graph.nodes.valueIterator()){
//            Log.d("MainActivityTest", "Trying nav to $node")
////            navHostController.navigate(node.route)
//        }
    }
}
