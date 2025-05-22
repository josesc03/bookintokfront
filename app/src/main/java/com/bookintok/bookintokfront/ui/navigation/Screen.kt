package com.bookintok.bookintokfront.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Location : Screen("location")
    object Point : Screen("point")
    object Province : Screen("province")

    // TODO: DEMAS PANTALLAS
}