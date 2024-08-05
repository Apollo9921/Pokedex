package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Type(
    val slot: Int,
    val type: TypeX
)