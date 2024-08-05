package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class VersionGroup(
    val name: String,
    val url: String
)