package com.bookintok.bookintokfront.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Login : Screen("login")
    object Register : Screen("register")

    object Main : Screen("main")

    object Location : Screen("location")
    object Point : Screen("point")
    object Province : Screen("province")

    object ProfilePage : Screen("profile/{userUid}") {
        fun createRoute(userUid: String?): String {
            return "profile/$userUid"
        }
    }

    object EditBook : Screen("editbook?bookId={bookId}") {
        fun createRoute(bookId: String?): String {
            return if (bookId != null) {
                "editbook?bookId=$bookId"
            } else {
                "editbook"
            }
        }
    }

    object Chats : Screen("chats")

    // TODO: DEMAS PANTALLAS
}