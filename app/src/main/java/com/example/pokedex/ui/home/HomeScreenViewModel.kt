package com.example.pokedex.ui.home

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.status
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

    private val _getPokemon = MutableStateFlow<PokemonResponse>(PokemonResponse.Loading)
    private val getPokemon: StateFlow<PokemonResponse> = _getPokemon

    private val _getPokemonDetails =
        MutableStateFlow<PokemonDetailsResponse>(PokemonDetailsResponse.Loading)
    private val getPokemonDetails: StateFlow<PokemonDetailsResponse> = _getPokemonDetails

    var isLoading = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isSuccessDetails = mutableStateOf(false)
    var pokemon: Pokemons? = null
    var pokemonDetails = SnapshotStateList<PokemonDetails>()
    var message = mutableStateOf("")
    var isPaginationInProgress = mutableStateOf(false)

    private var offset = mutableIntStateOf(0)
    private var limit = mutableIntStateOf(20)

    sealed class PokemonResponse {
        data object Loading : PokemonResponse()
        data class Success(val pokemon: Pokemons?) : PokemonResponse()
        data class Error(val message: String) : PokemonResponse()
        data object Finish : PokemonResponse()
    }

    sealed class PokemonDetailsResponse {
        data class Success(val pokemonDetails: SnapshotStateList<PokemonDetails>) : PokemonDetailsResponse()
        data class Error(val message: String) : PokemonDetailsResponse()

        data object Loading : PokemonDetailsResponse()
    }

    init {
        getPokemon()
    }

    fun getPokemon() {
        _getPokemon.value = PokemonResponse.Loading
        viewModelScope.launch {
            try {
                if (status == ConnectivityObserver.Status.Unavailable) {
                    _getPokemon.value = PokemonResponse.Error("No Internet Connection")
                    return@launch
                }
                val response = pokemonRepository.getListOfPokemons(limit.intValue, offset.intValue)
                if (response.status.value in 200..299) {
                    _getPokemon.value = PokemonResponse.Success(response.body())
                } else {
                    val errorMessage = when (response.status.value) {
                        400 -> "Bad Request"
                        401 -> "Unauthorized"
                        404 -> "Not Found"
                        else -> "Something went wrong"
                    }
                    _getPokemon.value = PokemonResponse.Error(errorMessage)
                }
            } catch (e: Exception) {
                _getPokemon.value = PokemonResponse.Error("${e.message}")
            }
        }
        getResponse()
    }

    private fun getResponse() {
        viewModelScope.launch {
            getPokemon.collect {
                when (it) {
                    is PokemonResponse.Error -> {
                        isLoading.value = false
                        isError.value = true
                        message.value = it.message
                    }

                    PokemonResponse.Loading -> {
                        isLoading.value = true
                        isError.value = false
                        message.value = ""
                    }

                    is PokemonResponse.Success -> {
                        pokemon = it.pokemon
                        isError.value = false
                        message.value = ""
                        isLoading.value = false
                        isSuccess.value = true
                        isPaginationInProgress.value = false
                    }

                    PokemonResponse.Finish -> {
                        isLoading.value = false
                        isError.value = false
                        message.value = ""
                        isSuccess.value = false
                    }
                }
            }
        }
    }

    fun getPokemonImage() {
        _getPokemon.value = PokemonResponse.Finish
        viewModelScope.launch {
            pokemon?.results?.forEachIndexed { index, _ ->
                val response =
                    pokemonRepository.getPokemonByImage(pokemon?.results?.get(index)?.url ?: "")
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
                    _getPokemonDetails.value =
                        PokemonDetailsResponse.Error("Something went wrong, please try again later")
                    message.value = "Something went wrong, please try again later"
                }
            }
            _getPokemonDetails.value = PokemonDetailsResponse.Success(pokemonDetails)
            getImageResponse()
        }
    }

    private fun getImageResponse() {
        viewModelScope.launch {
            getPokemonDetails.collect {
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
            getPokemon()
        }
    }
}