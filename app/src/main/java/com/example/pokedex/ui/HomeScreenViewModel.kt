package com.example.pokedex.ui

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.koin.PokemonRepository
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.model.pokemonsList.Pokemons
import com.example.pokedex.network.ConnectivityObserver
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
    var pokemons: Pokemons? = null
    var pokemonDetails = SnapshotStateList<PokemonDetails>()
    var message = mutableStateOf("")

    private var offset = mutableIntStateOf(0)
    private var limit = mutableIntStateOf(20)

    sealed class PokemonsResponse {
        data object Loading : PokemonsResponse()
        data class Success(val pokemons: Pokemons) : PokemonsResponse()
        data class Error(val message: String) : PokemonsResponse()
    }

    sealed class PokemonDetailsResponse {
        data class Success(val pokemonDetails: SnapshotStateList<PokemonDetails>) : PokemonDetailsResponse()
        data class Error(val message: String) : PokemonDetailsResponse()

        data object Loading : PokemonDetailsResponse()
    }

    fun getPokemons(status: ConnectivityObserver.Status) {
        if (status == ConnectivityObserver.Status.Unavailable) {
            isError.value = true
            _getPokemons.value = PokemonsResponse.Error("No internet connection")
            message.value = "No internet connection"
            return
        }
        if (pokemons == null) { _getPokemons.value = PokemonsResponse.Loading }
        viewModelScope.launch {
            val response = pokemonRepository.getListOfPokemons(limit.intValue, offset.intValue)
            if (response.status.value in 200..299) {
                _getPokemons.value = PokemonsResponse.Success(response.body())
            } else {
                _getPokemons.value =
                    PokemonsResponse.Error("Something went wrong, please try again later")
                message.value = "Something went wrong, please try again later"
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
                        isError.value = false
                        message.value = ""
                        isLoading.value = false
                        isSuccess.value = true
                        pokemons = it.pokemons
                    }
                }
            }
        }
    }

    fun getPokemonsImage() {
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
                        message.value = it.message
                    }

                    is PokemonDetailsResponse.Success -> {
                        isLoading.value = false
                        isError.value = false
                        message.value = ""
                    }

                    PokemonDetailsResponse.Loading -> {
                        isError.value = false
                        message.value = ""
                    }
                }
            }
        }
    }
}