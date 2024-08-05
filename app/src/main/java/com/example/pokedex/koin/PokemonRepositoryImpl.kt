package com.example.pokedex.koin

import com.example.pokedex.ktor.PokemonApi
import io.ktor.client.statement.HttpResponse

class PokemonRepositoryImpl(
    private val pokemonApi: PokemonApi
): PokemonRepository {
    override suspend fun getListOfPokemons(limit: Int, offset: Int): HttpResponse {
        return pokemonApi.getListOfPokemons(limit, offset)
    }
}