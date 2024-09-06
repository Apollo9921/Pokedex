package com.example.pokedex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.navigation.CustomNavType.PokemonDetailsType
import com.example.pokedex.ui.detail.DetailScreen
import com.example.pokedex.ui.home.HomeScreen
import kotlin.reflect.typeOf

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: Destination = Destination.Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Destination.Home> {
            HomeScreen(navController)
        }
        composable<Destination.Details>(
            typeMap = mapOf(
                typeOf<PokemonDetails>() to PokemonDetailsType
            )
        ) {
            val arguments = it.toRoute<Destination.Details>()
            DetailScreen(navController, arguments.details)
        }
    }
}