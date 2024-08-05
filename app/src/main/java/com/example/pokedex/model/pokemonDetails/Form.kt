package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val name: String,
    val url: String
)