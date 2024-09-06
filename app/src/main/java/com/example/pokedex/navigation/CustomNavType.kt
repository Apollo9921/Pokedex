package com.example.pokedex.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {
    val PokemonDetailsType = object : NavType<PokemonDetails>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): PokemonDetails? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): PokemonDetails {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: PokemonDetails): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: PokemonDetails) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}