package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class GameIndice(
    val game_index: Int,
    val version: Version
)