package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class OfficialArtwork(
    val front_default: String,
    val front_shiny: String
)