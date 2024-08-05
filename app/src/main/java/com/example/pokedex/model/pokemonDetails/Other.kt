package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Other(
    val dream_world: DreamWorld,
    val home: Home,
    @SerialName("official-artwork")
    val official_artwork: OfficialArtwork,
    val showdown: Showdown
)