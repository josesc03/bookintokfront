package com.bookintok.bookintokfront.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Preview(showBackground = true)
@Composable
fun ChatsScreenPreview() {
    ChatsScreen(navController = rememberNavController())
}

@Composable
fun ChatsScreen(navController: NavController) {
    var userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
//        getUserFromApi(
//            onSuccess = { user = it },
//            onError = { error = it }
//        )
//
//        getChatsFromUserApi(
//            onSuccess = { libros = it },
//            onError = { error = it },
//            filters = getAppliedFilters()
//        )
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Box(
                modifier = Modifier
                    .background(Color(0xffb3d0be))
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "BOOKINTOK",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                )

            }

            Column {



            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                MenuInferior(navController = navController, 2, userUid)
            }

        }

    }

}