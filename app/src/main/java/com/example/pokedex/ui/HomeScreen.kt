package com.example.pokedex.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.pokedex.R
import com.example.pokedex.core.BackgroundColor
import com.example.pokedex.core.Black
import com.example.pokedex.core.TypeGrey
import com.example.pokedex.core.Types
import com.example.pokedex.core.definePokemonTypes
import com.example.pokedex.core.mediaQueryWidth
import com.example.pokedex.core.normal
import com.example.pokedex.core.small
import com.example.pokedex.main.keepSplashOpened
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.model.pokemonsList.Result
import com.example.pokedex.network.ConnectivityObserver
import com.example.pokedex.network.NetworkConnectivityObserver
import com.example.pokedex.ui.homeList.DisplayPokemonList
import com.example.pokedex.ui.homeList.DisplayPokemonMosaicList
import org.koin.androidx.compose.koinViewModel

private lateinit var connectivityObserver: ConnectivityObserver
private var applicationContext: Context? = null
private lateinit var status: ConnectivityObserver.Status
private var pokemonTypes: HashMap<Types, Color> = hashMapOf(Types.Normal to TypeGrey)
private var pokemonList = SnapshotStateList<Result>()
private var pokemonDetails = SnapshotStateList<PokemonDetails>()
private lateinit var viewModel: HomeScreenViewModel
private var rotateDisplayMode = mutableFloatStateOf(90f)

@Composable
fun HomeScreen(navController: NavHostController) {
    applicationContext = LocalContext.current.applicationContext
    connectivityObserver = NetworkConnectivityObserver(applicationContext ?: return)
    status = connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    ).value
    viewModel = koinViewModel<HomeScreenViewModel>()
    val staggeredGridState = rememberLazyStaggeredGridState()
    val state = rememberLazyListState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { PokemonTopAppBar() },
        bottomBar = { PokemonBottomAppBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            GetPokemonList()
            if (rotateDisplayMode.floatValue == 90f) {
                val lastState = remember { derivedStateOf { state.firstVisibleItemIndex } }
                DisplayPokemonMosaicList(
                    staggeredGridState,
                    lastState,
                    navController,
                    pokemonList,
                    pokemonDetails,
                    pokemonTypes,
                    viewModel
                )
            } else {
                val lastState =
                    remember { derivedStateOf { staggeredGridState.firstVisibleItemIndex } }
                DisplayPokemonList(
                    state,
                    lastState,
                    navController,
                    pokemonList,
                    pokemonDetails,
                    pokemonTypes,
                    viewModel
                )
            }
        }
    }
    keepSplashOpened = false
    definePokemonTypes(pokemonTypes)
}

@Composable
private fun PokemonTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundColor)
            .padding(top = 60.dp, start = 20.dp, end = 20.dp),
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
                .rotate(rotateDisplayMode.floatValue)
                .clickable {
                    if (rotateDisplayMode.floatValue == 90f) {
                        rotateDisplayMode.floatValue = 0f
                    } else {
                        rotateDisplayMode.floatValue = 90f
                    }
                }
        )
    }
}

@Composable
private fun PokemonBottomAppBar() {

}

@Composable
private fun GetPokemonList() {
    when {
        viewModel.isLoading.value -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor),
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
                    .background(BackgroundColor),
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
            pokemonList.addAll(viewModel.pokemons?.results ?: emptyList())
            if (pokemonList.last().url.isNotEmpty()) {
                viewModel.getPokemonsImage()
            } else {
                pokemonList.removeLast()
            }
        }

        viewModel.isSuccessDetails.value -> {
            pokemonDetails.add(viewModel.pokemonDetails.last())
        }
    }
}