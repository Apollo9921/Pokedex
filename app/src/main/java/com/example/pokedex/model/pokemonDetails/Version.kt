package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Version(
    val name: String,
    val url: String
)