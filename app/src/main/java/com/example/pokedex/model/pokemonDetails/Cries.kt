package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Cries(
    val latest: String,
    val legacy: String
)