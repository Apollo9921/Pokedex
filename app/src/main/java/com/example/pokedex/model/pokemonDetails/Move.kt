package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val move: MoveX,
    val version_group_details: List<VersionGroupDetail>
)