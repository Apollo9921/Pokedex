package com.example.pokedex.ui.homeList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.pokedex.R
import com.example.pokedex.core.Black
import com.example.pokedex.core.TypeGrey
import com.example.pokedex.core.Types
import com.example.pokedex.core.White
import com.example.pokedex.core.mediaQueryWidth
import com.example.pokedex.core.normal
import com.example.pokedex.core.small
import com.example.pokedex.model.pokemonDetails.PokemonDetails
import com.example.pokedex.model.pokemonsList.Result
import com.example.pokedex.ui.HomeScreenViewModel

import java.util.Locale

@Composable
fun DisplayPokemonMosaicList(
    staggeredGridState: LazyStaggeredGridState,
    lastState: State<Int>,
    navController: NavHostController,
    pokemonList: SnapshotStateList<Result>,
    pokemonDetails: SnapshotStateList<PokemonDetails>,
    pokemonTypes: HashMap<Types, Color>,
    viewModel: HomeScreenViewModel
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = staggeredGridState,
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
                        text = pokemonList[index].name.uppercase(),
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
    LaunchedEffect(lastState.value) {
        staggeredGridState.scrollToItem(lastState.value)
    }
    Pagination(staggeredGridState, null, pokemonList, viewModel)
}

@Composable
fun DisplayPokemonList(
    state: LazyListState,
    lastState: State<Int>,
    navController: NavHostController,
    pokemonList: SnapshotStateList<Result>,
    pokemonDetails: SnapshotStateList<PokemonDetails>,
    pokemonTypes: HashMap<Types, Color>,
    viewModel: HomeScreenViewModel
) {
    LazyColumn(
        state = state,
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
            val height =
                if (pokemonDetails.size - 1 >= index) pokemonDetails[index].height / 10.0 else 0.0
            val weight =
                if (pokemonDetails.size - 1 >= index) pokemonDetails[index].weight / 10.0 else 0.0
            val type =
                if (pokemonDetails.size - 1 >= index) pokemonDetails[index].types.firstOrNull()?.type?.name
                    ?: Types.Unknown.name else Types.Unknown.name
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = color.values.first(),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
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
                        Spacer(modifier = Modifier.padding(20.dp))
                        Column {
                            Text(
                                text = pokemonList[index].name.uppercase(),
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
                            Spacer(modifier = Modifier.padding(10.dp))
                            Text(
                                text = "Type: $type",
                                color = White,
                                fontSize =
                                if (mediaQueryWidth() <= small) {
                                    18.sp
                                } else if (mediaQueryWidth() <= normal) {
                                    22.sp
                                } else {
                                    26.sp
                                },
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$height m",
                            color = White,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                14.sp
                            } else if (mediaQueryWidth() <= normal) {
                                18.sp
                            } else {
                                22.sp
                            },
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Text(
                            text = "$weight kg",
                            color = White,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                14.sp
                            } else if (mediaQueryWidth() <= normal) {
                                18.sp
                            } else {
                                22.sp
                            },
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
        }
    }
    LaunchedEffect(lastState.value) {
        state.scrollToItem(lastState.value)
    }
    Pagination(null, state, pokemonList, viewModel)
}

@Composable
private fun Pagination(
    state: LazyStaggeredGridState?,
    state2: LazyListState?,
    pokemonList: SnapshotStateList<Result>,
    viewModel: HomeScreenViewModel
) {
    with(viewModel) {
        if (state != null) {
            if (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == pokemonList.size - 1 &&
                !isPaginationInProgress.value && pokemonDetails.size >= pokemonList.size
            ) {
                isPaginationInProgress.value = true
                pagination()
            }
        } else if (state2 != null) {
            if (state2.layoutInfo.visibleItemsInfo.lastOrNull()?.index == pokemonList.size - 1 &&
                !isPaginationInProgress.value && pokemonDetails.size >= pokemonList.size
            ) {
                isPaginationInProgress.value = true
                pagination()
            }
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