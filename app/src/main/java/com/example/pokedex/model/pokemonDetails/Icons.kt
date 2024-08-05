package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Icons(
    val front_default: String,
    val front_female: String?
)