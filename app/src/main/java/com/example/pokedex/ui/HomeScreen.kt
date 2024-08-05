package com.example.pokedex.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.pokedex.keepSplashOpened

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = { },
        bottomBar = { }
    ) {
        DisplayPokemonList(navController, it)
    }
    keepSplashOpened = false
}

@Composable
private fun DisplayPokemonList(navController: NavHostController, it: PaddingValues) {
    Text(
        text = "Pokemon List",
        modifier = Modifier.padding(it)
    )
}