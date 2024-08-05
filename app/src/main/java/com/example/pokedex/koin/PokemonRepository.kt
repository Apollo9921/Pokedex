package com.example.pokedex.koin

import io.ktor.client.statement.HttpResponse

interface PokemonRepository {
    suspend fun getListOfPokemons(limit: Int, offset: Int): HttpResponse
}