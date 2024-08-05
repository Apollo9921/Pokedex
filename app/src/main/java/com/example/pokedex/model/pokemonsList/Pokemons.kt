package com.example.pokedex.model.pokemonsList

import kotlinx.serialization.Serializable

@Serializable
data class Pokemons(
    val count: Int,
    val next: String,
    val previous: String?,
    val results: ArrayList<Result>
)