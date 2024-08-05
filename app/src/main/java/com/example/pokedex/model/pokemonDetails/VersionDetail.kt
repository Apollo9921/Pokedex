package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class VersionDetail(
    val rarity: Int,
    val version: VersionX
)