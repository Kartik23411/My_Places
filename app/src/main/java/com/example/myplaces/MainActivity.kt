package com.example.myplaces

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myplaces.screens.AddMyPlacesScreen
import com.example.myplaces.screens.DetailsScreen
import com.example.myplaces.screens.HomeScreen
import com.example.myplaces.screens.Map
import com.example.myplaces.ui.theme.MyPlacesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MyPlacesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(navController, Screen.Home.route)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startScreen: String
) {
    NavHost(navController, startScreen) {

        composable(Screen.Home.route) {
            HomeScreen(
                onFabClick = { navController.navigate(Screen.Add.route) },
                onClick = { navController.navigate("${Screen.DetailScreen.route}/$it") })
        }
        composable(Screen.Add.route) {
            AddMyPlacesScreen(
                onSave = { navController.navigate(Screen.Home.route) },
                onNavBack = { navController.navigate(Screen.Home.route) })
        }
        composable("${Screen.DetailScreen.route}/{id}") {
            val idStr = it.arguments?.getString("id")
            val id: Int? = idStr?.toIntOrNull()
            if (id != null) {
                DetailsScreen(id, onMapClicked = {navController.navigate("${Screen.MapScreen.route}/$it")
                }, onNavButtonClick = { navController.navigateUp() })
            }
        }
        composable("${Screen.MapScreen.route}/{id}") {
            val idStr = it.arguments?.getString("id")
            val id: Int? = idStr?.toIntOrNull()
            if (id != null) {
                Map(id)
            }
        }
    }
}
