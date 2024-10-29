package com.example.myplaces

sealed class Screen(val route: String){

    data object Home: Screen("home")
    data object Add: Screen("add")
    data object DetailScreen: Screen("detailscreen")
    data object MapScreen: Screen("mapscreen")
}