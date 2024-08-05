package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class DreamWorld(
    val front_default: String,
    val front_female: String?
)