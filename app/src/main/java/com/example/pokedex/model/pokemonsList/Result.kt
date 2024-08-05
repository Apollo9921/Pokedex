package com.example.pokedex.model.pokemonsList

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val name: String,
    val url: String
)