package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Ability(
    val ability: AbilityX,
    val is_hidden: Boolean,
    val slot: Int
)