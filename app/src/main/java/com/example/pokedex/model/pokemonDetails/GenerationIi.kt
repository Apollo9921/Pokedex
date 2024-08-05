package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class GenerationIi(
    val crystal: Crystal,
    val gold: Gold,
    val silver: Silver
)