package com.example.pokedex.ui

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.koin.PokemonRepository
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.model.pokemonsList.Pokemons
import io.ktor.client.call.body
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _getPokemons = MutableStateFlow<PokemonsResponse>(PokemonsResponse.Loading)
    private val getPokemons: StateFlow<PokemonsResponse> = _getPokemons

    private val _getPokemonsDetails =
        MutableStateFlow<PokemonDetailsResponse>(PokemonDetailsResponse.Loading)
    private val getPokemonsDetails: StateFlow<PokemonDetailsResponse> = _getPokemonsDetails

    var isLoading = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isSuccessDetails = mutableStateOf(false)
    var pokemons: Pokemons? = null
    var pokemonDetails = SnapshotStateList<PokemonDetails>()
    var message = mutableStateOf("")
    var isPaginationInProgress = mutableStateOf(false)

    private var offset = mutableIntStateOf(0)
    private var limit = mutableIntStateOf(20)

    sealed class PokemonsResponse {
        data object Loading : PokemonsResponse()
        data class Success(val pokemons: Pokemons?) : PokemonsResponse()
        data class Error(val message: String) : PokemonsResponse()
        data object Finish : PokemonsResponse()
    }

    sealed class PokemonDetailsResponse {
        data class Success(val pokemonDetails: SnapshotStateList<PokemonDetails>) : PokemonDetailsResponse()
        data class Error(val message: String) : PokemonDetailsResponse()

        data object Loading : PokemonDetailsResponse()
    }

    init {
        getPokemons()
    }

    private fun getPokemons() {
        _getPokemons.value = PokemonsResponse.Loading
        viewModelScope.launch {
            try {
                val response = pokemonRepository.getListOfPokemons(limit.intValue, offset.intValue)
                if (response.status.value in 200..299) {
                    _getPokemons.value = PokemonsResponse.Success(response.body())
                } else {
                    val errorMessage = when (response.status.value) {
                        400 -> "Bad Request"
                        401 -> "Unauthorized"
                        404 -> "Not Found"
                        else -> "Something went wrong"
                    }
                    _getPokemons.value = PokemonsResponse.Error(errorMessage)
                    message.value = errorMessage
                }
            } catch (e: Exception) {
                _getPokemons.value = PokemonsResponse.Error("Network error: ${e.message}")
                message.value = "Network error: ${e.message}"
            }
            getResponse()
        }
    }

    private fun getResponse() {
        viewModelScope.launch {
            getPokemons.collect {
                when (it) {
                    is PokemonsResponse.Error -> {
                        isLoading.value = false
                        isError.value = true
                        message.value = it.message
                    }

                    PokemonsResponse.Loading -> {
                        isLoading.value = true
                        isError.value = false
                        message.value = ""
                    }

                    is PokemonsResponse.Success -> {
                        pokemons = it.pokemons
                        isError.value = false
                        message.value = ""
                        isLoading.value = false
                        isSuccess.value = true
                        isPaginationInProgress.value = false
                    }

                    PokemonsResponse.Finish -> {
                        isLoading.value = false
                        isError.value = false
                        message.value = ""
                        isSuccess.value = false
                    }
                }
            }
        }
    }

    fun getPokemonsImage() {
        _getPokemons.value = PokemonsResponse.Finish
        viewModelScope.launch {
            pokemons?.results?.forEachIndexed { index, _ ->
                val response =
                    pokemonRepository.getPokemonByImage(pokemons?.results?.get(index)?.url ?: "")
                if (response.status.value in 200..299) {
                    if (pokemonDetails.isNotEmpty()) {
                        if (!pokemonDetails.contains(response.body())) {
                            pokemonDetails.add(response.body())
                        }
                    } else {
                        pokemonDetails.add(response.body())
                    }
                    isSuccessDetails.value = true
                } else {
                    _getPokemonsDetails.value =
                        PokemonDetailsResponse.Error("Something went wrong, please try again later")
                    message.value = "Something went wrong, please try again later"
                }
            }
            _getPokemonsDetails.value = PokemonDetailsResponse.Success(pokemonDetails)
            getImageResponse()
        }
    }

    private fun getImageResponse() {
        viewModelScope.launch {
            getPokemonsDetails.collect {
                when (it) {
                    is PokemonDetailsResponse.Error -> {
                        isLoading.value = false
                        isError.value = true
                        isSuccess.value = false
                        isSuccessDetails.value = false
                        message.value = it.message
                    }

                    is PokemonDetailsResponse.Success -> {
                        isLoading.value = false
                        isError.value = false
                        message.value = ""
                        isSuccess.value = false
                    }

                    PokemonDetailsResponse.Loading -> {
                        isError.value = false
                        message.value = ""
                        isSuccess.value = false
                        isSuccessDetails.value = false
                    }
                }
            }
        }
    }

    fun pagination() {
        viewModelScope.launch {
            offset.intValue += 20
            getPokemons()
        }
    }
}