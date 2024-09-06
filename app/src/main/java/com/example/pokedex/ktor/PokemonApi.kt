package com.example.pokedex.ktor

import com.example.pokedex.BuildConfig
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PokemonApi {
    suspend fun getListOfPokemon(limit: Int, offset: Int): HttpResponse =
        KtorClient.httpClient.get {
            url("${BuildConfig.BASE_URL}pokemon?limit=$limit&offset=$offset")
            contentType(ContentType.Application.Json)
        }


    suspend fun getPokemonImage(url: String): HttpResponse =
        KtorClient.httpClient.get {
            url(url)
            contentType(ContentType.Application.Json)
        }
}