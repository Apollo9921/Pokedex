package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Sprites(
    val other: Other,
    //val versions: Versions
)