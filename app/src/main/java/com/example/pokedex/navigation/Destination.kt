package com.example.pokedex.navigation

import com.example.pokedex.model.pokemonDetails.PokemonDetails
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    data object Home: Destination()
    @Serializable
    data class Details(val details: PokemonDetails): Destination()
}