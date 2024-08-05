package com.example.pokedex.ktor

import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PokemonApi {
    suspend fun getListOfPokemons(limit: Int, offset: Int): HttpResponse =
        KtorClient.httpClient.get {
            url("https://pokeapi.co/api/v2/pokemon?limit=$limit&offset=$offset")
            contentType(ContentType.Application.Json)
        }


    suspend fun getPokemonImage(url: String): HttpResponse =
        KtorClient.httpClient.get {
            url(url)
            contentType(ContentType.Application.Json)
        }
}