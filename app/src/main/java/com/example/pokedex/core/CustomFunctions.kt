package com.example.pokedex.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.network.ConnectivityObserver
import java.util.Locale

val small = 600.dp
val normal = 840.dp
lateinit var status: ConnectivityObserver.Status

@Composable
fun mediaQueryWidth(): Dp {
    return LocalContext.current.resources.displayMetrics.widthPixels.dp / LocalDensity.current.density
}

enum class Types {
    Normal,
    Fighting,
    Flying,
    Poison,
    Ground,
    Rock,
    Bug,
    Ghost,
    Steel,
    Fire,
    Water,
    Grass,
    Electric,
    Psychic,
    Ice,
    Dragon,
    Dark,
    Fairy,
    Stellar,
    Unknown
}

fun definePokemonTypes(pokemonTypes: HashMap<Types, Color>) {
    pokemonTypes[Types.Normal] = TypeGrey
    pokemonTypes[Types.Fighting] = TypeBrown
    pokemonTypes[Types.Flying] = TypeBlue
    pokemonTypes[Types.Poison] = TypePurple
    pokemonTypes[Types.Ground] = TypeRed
    pokemonTypes[Types.Rock] = TypeGrey
    pokemonTypes[Types.Bug] = TypeGreen
    pokemonTypes[Types.Ghost] = TypePurple
    pokemonTypes[Types.Steel] = TypeGrey
    pokemonTypes[Types.Fire] = TypeRed
    pokemonTypes[Types.Water] = TypeBlue
    pokemonTypes[Types.Grass] = TypeGreen
    pokemonTypes[Types.Electric] = TypeYellow
    pokemonTypes[Types.Psychic] = TypePink
    pokemonTypes[Types.Ice] = TypeBlue
    pokemonTypes[Types.Dragon] = TypePurple
    pokemonTypes[Types.Dark] = TypeBrown
    pokemonTypes[Types.Fairy] = TypePink
    pokemonTypes[Types.Stellar] = TypeYellow
    pokemonTypes[Types.Unknown] = TypeGrey
}

fun getPokemonTypeColor(type: HashMap<Types, Color>, details: PokemonDetails): Map<Types, Color> {
    return type.filter {
        val firstType = details.types.firstOrNull()
        firstType != null && firstType.type.name == it.key.name.lowercase(Locale.getDefault())
    }
}

fun getPokemonType(details: PokemonDetails): String {
    return details.types.firstOrNull()?.type?.name ?: Types.Unknown.name
}