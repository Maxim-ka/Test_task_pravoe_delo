package reschikov.test.pravoedelo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import reschikov.test.pravoedelo.ui.screens.code.GetTokenScreen
import reschikov.test.pravoedelo.ui.screens.token.GreetingsScreen
import reschikov.test.pravoedelo.ui.theme.TestPravoeDeloTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestPravoeDeloTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController, startDestination = NavRoutes.GetToken.route
    ) {
        composable(NavRoutes.GetToken.route) {
            GetTokenScreen { guest, token -> navController.navigate(NavRoutes.Greetings(guest, token).route) }
        }
        composable(NavRoutes.Greetings("{guest}", "{token}").route,
            arguments = setStringsArg("guest", "token")){ navBackStackEntry ->
            navBackStackEntry.getStringsArgs("guest", "token")?.let {
                GreetingsScreen(guest = it[0], token = it[1])
            }
        }
    }
}

private fun setStringsArg(vararg names: String): List<NamedNavArgument> {
    val list = mutableListOf<NamedNavArgument>()
    for (string in names){
        list.add(navArgument(string) {
            nullable = false
            type = NavType.StringType
        })
    }
    return list
}

@Composable
private fun NavBackStackEntry.getStringsArgs(vararg names: String): Array<String>? {
    arguments?.run {
        return Array(names.size) {
            getString(names[it]) ?: ""
        }
    }
    return null
}

sealed class NavRoutes(val route: String) {
    object GetToken : NavRoutes("get_token")
    class Greetings(guest: String, token: String) : NavRoutes("greetings/$guest/$token")
}


