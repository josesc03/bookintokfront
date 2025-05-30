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

    object DetailBook : Screen("detailbook?bookId={bookId}") {
        fun createRoute(bookId: String?): String {
            return if (bookId != null) {
                "detailbook?bookId=$bookId"
            } else {
                "detailbook"
            }
        }
    }

    object Chats : Screen("chats")

    object Chat : Screen("chatPage?idChat={idChat}") {
        fun createRoute(idChat: String?): String {
            return if (idChat != null) {
                "chatPage?idChat=$idChat"
            } else {
                "chatPage"
            }
        }
    }

    // TODO: DEMAS PANTALLAS
}