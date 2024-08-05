package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerationI(
    @SerialName("red-blue")
    val red_blue: RedBlue,
    val yellow: Yellow
)