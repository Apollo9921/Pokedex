package com.example.pokedex.koin

import com.example.pokedex.ktor.PokemonApi
import io.ktor.client.statement.HttpResponse

class PokemonRepositoryImpl(
    private val pokemonApi: PokemonApi
): PokemonRepository {
    override suspend fun getListOfPokemons(limit: Int, offset: Int): HttpResponse {
        return pokemonApi.getListOfPokemon(limit, offset)
    }

    override suspend fun getPokemonByImage(url: String): HttpResponse {
        return pokemonApi.getPokemonImage(url)
    }
}