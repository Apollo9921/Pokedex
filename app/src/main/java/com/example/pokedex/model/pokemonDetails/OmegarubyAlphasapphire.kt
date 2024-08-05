package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class OmegarubyAlphasapphire(
    val front_default: String,
    val front_female: String?,
    val front_shiny: String,
    val front_shiny_female: String?
)