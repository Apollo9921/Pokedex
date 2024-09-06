package com.example.pokedex.ui.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.pokedex.model.pokemonDetails.PokemonDetails

@Composable
fun DetailScreen(navHostController: NavHostController, details: PokemonDetails) {
    Text(text = details.name)
}