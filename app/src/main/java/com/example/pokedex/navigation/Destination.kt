package com.example.pokedex.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    data object Home: Destination()
    @Serializable
    data class Details(val id: Int): Destination()
}