package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class MoveLearnMethod(
    val name: String,
    val url: String
)