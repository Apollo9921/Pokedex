package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerationVii(
    val icons: Icons,
    @SerialName("ultra-sun-ultra-moon")
    val ultraSunUltraMoon: UltraSunUltraMoon
)