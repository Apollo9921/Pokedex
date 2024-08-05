package com.example.pokedex.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.pokedex.R
import com.example.pokedex.custom.BackgroundColor
import com.example.pokedex.custom.Black
import com.example.pokedex.custom.White
import com.example.pokedex.custom.mediaQueryWidth
import com.example.pokedex.custom.normal
import com.example.pokedex.custom.small
import com.example.pokedex.keepSplashOpened
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.model.pokemonsList.Pokemons
import com.example.pokedex.network.ConnectivityObserver
import com.example.pokedex.network.NetworkConnectivityObserver
import org.koin.androidx.compose.koinViewModel

private lateinit var connectivityObserver: ConnectivityObserver
private var applicationContext: Context? = null
private lateinit var status: ConnectivityObserver.Status

@Composable
fun HomeScreen(navController: NavHostController) {
    applicationContext = LocalContext.current.applicationContext
    connectivityObserver = NetworkConnectivityObserver(applicationContext ?: return)
    status = connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    ).value
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        topBar = { PokemonTopAppBar() },
        bottomBar = { }
    ) {
        GetPokemonList(navController, it)
    }
    keepSplashOpened = false
}

@Composable
private fun PokemonTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            color = Black,
            fontWeight = FontWeight.Bold,
            fontSize =
            if (mediaQueryWidth() <= small) {
                20.sp
            } else if (mediaQueryWidth() <= normal) {
                24.sp
            } else {
                28.sp
            }
        )
        Image(
            painter = painterResource(id = R.drawable.display_mode),
            contentDescription = null,
            modifier = Modifier
                .size(
                    if (mediaQueryWidth() <= small) {
                        20.dp
                    } else if (mediaQueryWidth() <= normal) {
                        30.dp
                    } else {
                        40.dp
                    }
                )
                .rotate(90f)
                .clickable { }
        )
    }
}

@Composable
private fun GetPokemonList(navController: NavHostController, it: PaddingValues) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    viewModel.getPokemons(status)
    when {
        viewModel.isLoading.value -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Black
                )
            }
        }

        viewModel.isError.value -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.message.value,
                    color = Black,
                    fontSize =
                    if (mediaQueryWidth() <= small) {
                        20.sp
                    } else if (mediaQueryWidth() <= normal) {
                        24.sp
                    } else {
                        28.sp
                    },
                    textAlign = TextAlign.Center
                )
            }
        }

        viewModel.isSuccess.value -> {
            DisplayPokemonList(navController, it, viewModel.pokemons ?: return, viewModel.pokemonDetails)
        }
    }
}

@Composable
private fun DisplayPokemonList(
    navController: NavHostController,
    it: PaddingValues,
    pokemons: Pokemons,
    pokemonDetails: ArrayList<PokemonDetails>
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = it,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(20.dp)
    ) {
        items(pokemons.results.size) { index ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pokemonDetails[index].sprites.other.dream_world.front_default)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = null,
                        error = painterResource(id = R.drawable.logo),
                        modifier = Modifier
                            .size(
                                if (mediaQueryWidth() <= small) {
                                    100.dp
                                } else if (mediaQueryWidth() <= normal) {
                                    150.dp
                                } else {
                                    200.dp
                                }
                            )
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(
                        text = pokemons.results[index].name,
                        color = White,
                        fontSize =
                        if (mediaQueryWidth() <= small) {
                            20.sp
                        } else if (mediaQueryWidth() <= normal) {
                            24.sp
                        } else {
                            28.sp
                        },
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W400
                    )
                }
            }
        }
    }
}