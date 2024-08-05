package com.example.pokedex.model.pokemonDetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Versions(
    @SerialName("generation-i")
    val generationI: GenerationI,
    @SerialName("generation-ii")
    val generationII: GenerationIi,
    @SerialName("generation-iii")
    val generationIII: GenerationIii,
    @SerialName("generation-iv")
    val generationIV: GenerationIv,
    @SerialName("generation-v")
    val generationV: GenerationV,
    @SerialName("generation-vi")
    val generationVI: GenerationVi,
    @SerialName("generation-vii")
    val generationVII: GenerationVii,
    @SerialName("generation-viii")
    val generationVIII: GenerationViii
)