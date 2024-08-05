package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class PastTypes(
    val generation: Generation,
    val types: List<TypeXX>
)