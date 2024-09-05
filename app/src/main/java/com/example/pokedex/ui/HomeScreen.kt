package com.example.pokedex.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.pokedex.R
import com.example.pokedex.custom.BackgroundColor
import com.example.pokedex.custom.Black
import com.example.pokedex.custom.TypeGrey
import com.example.pokedex.custom.Types
import com.example.pokedex.custom.White
import com.example.pokedex.custom.definePokemonTypes
import com.example.pokedex.custom.mediaQueryWidth
import com.example.pokedex.custom.normal
import com.example.pokedex.custom.small
import com.example.pokedex.keepSplashOpened
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.model.pokemonsList.Result
import com.example.pokedex.network.ConnectivityObserver
import com.example.pokedex.network.NetworkConnectivityObserver
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

private lateinit var connectivityObserver: ConnectivityObserver
private var applicationContext: Context? = null
private lateinit var status: ConnectivityObserver.Status
private var pokemonTypes: HashMap<Types, Color> = hashMapOf(Types.Normal to TypeGrey)
private var pokemonList = SnapshotStateList<Result>()
private var pokemonDetails = SnapshotStateList<PokemonDetails>()
private lateinit var viewModel: HomeScreenViewModel

@Composable
fun HomeScreen(navController: NavHostController) {
    applicationContext = LocalContext.current.applicationContext
    connectivityObserver = NetworkConnectivityObserver(applicationContext ?: return)
    status = connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    ).value
    viewModel = koinViewModel<HomeScreenViewModel>()
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
            DisplayPokemonList(navController)
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
                .rotate(90f)
                .clickable { }
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

@Composable
private fun DisplayPokemonList(navController: NavHostController) {
    val state = rememberLazyStaggeredGridState()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = state,
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        items(pokemonList.size) { index ->
            val pokemonImage =
                pokemonDetails.firstOrNull { it.name == pokemonList[index].name }
            val color = if (pokemonDetails.size > index) {
                pokemonTypes.filter {
                    val firstType = pokemonDetails[index].types.firstOrNull()
                    firstType != null && firstType.type.name == it.key.name.lowercase(Locale.getDefault())
                }
            } else {
                hashMapOf(Types.Unknown to TypeGrey)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = color.values.first(),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (pokemonImage != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pokemonImage.sprites.other.dream_world.front_default)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = null,
                            error = painterResource(id = R.drawable.logo),
                            placeholder = painterResource(id = R.drawable.logo),
                            modifier = Modifier
                                .size(
                                    if (mediaQueryWidth() <= small) {
                                        80.dp
                                    } else if (mediaQueryWidth() <= normal) {
                                        130.dp
                                    } else {
                                        180.dp
                                    }
                                )
                        )
                    } else {
                        CircularProgressIndicator(
                            color = White,
                            modifier = Modifier
                                .size(
                                    if (mediaQueryWidth() <= small) {
                                        80.dp
                                    } else if (mediaQueryWidth() <= normal) {
                                        130.dp
                                    } else {
                                        180.dp
                                    }
                                )
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                    Text(
                        text = pokemonList[index].name,
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
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    with(viewModel) {
        if (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == pokemonList.size - 1 &&
            !isPaginationInProgress.value && pokemonDetails.size >= pokemonList.size
        ) {
            isPaginationInProgress.value = true
            pagination()
        }
    }

    if (viewModel.isPaginationInProgress.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            CircularProgressIndicator(
                color = Black,
                modifier = Modifier.size(
                    when (mediaQueryWidth()) {
                        in 0.dp..small -> 40.dp
                        in small..normal -> 90.dp
                        else -> 140.dp
                    }
                )
            )
        }
    }
}