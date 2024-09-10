package com.example.pokedex.ui.detail

import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.pokedex.R
import com.example.pokedex.core.Black
import com.example.pokedex.core.Grey
import com.example.pokedex.core.TypeGrey
import com.example.pokedex.core.Types
import com.example.pokedex.core.White
import com.example.pokedex.core.definePokemonTypes
import com.example.pokedex.core.getPokemonType
import com.example.pokedex.core.getPokemonTypeColor
import com.example.pokedex.core.mediaQueryWidth
import com.example.pokedex.core.normal
import com.example.pokedex.core.small
import com.example.pokedex.model.pokemonDetails.PokemonDetails

private var pokemonTypes: HashMap<Types, Color> = hashMapOf(Types.Normal to TypeGrey)

@Composable
fun DetailScreen(navHostController: NavHostController, details: PokemonDetails) {
    definePokemonTypes(pokemonTypes)
    Scaffold(
        topBar = { TopDetailsBar(navHostController, details) }
    ) { paddingValues ->
        DetailsContent(paddingValues, details)
    }
}

@Composable
private fun TopDetailsBar(navHostController: NavHostController, details: PokemonDetails) {
    val color = getPokemonTypeColor(pokemonTypes, details)
    val type = getPokemonType(details)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.values.firstOrNull() ?: TypeGrey)
            .padding(top = 60.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)

    ) {
        Column {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(
                            if (mediaQueryWidth() <= small) {
                                30.dp
                            } else if (mediaQueryWidth() <= normal) {
                                40.dp
                            } else {
                                50.dp
                            }
                        )
                        .clickable { navHostController.navigateUp() }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = details.name.uppercase(),
                        color = White,
                        fontWeight = FontWeight.Bold,
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
                    Text(
                        text = type,
                        color = White,
                        fontWeight = FontWeight.Bold,
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
            Spacer(modifier = Modifier.padding(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(details.sprites.other.showdown.front_default)
                        .decoderFactory(
                            if (SDK_INT >= 28) {
                                ImageDecoderDecoder.Factory()
                            } else {
                                GifDecoder.Factory()
                            }
                        )
                        .build(),
                    contentDescription = null,
                    error = painterResource(id = R.drawable.logo),
                    placeholder = painterResource(id = R.drawable.logo),
                    modifier = Modifier
                        .size(
                            if (mediaQueryWidth() <= small) {
                                150.dp
                            } else if (mediaQueryWidth() <= normal) {
                                200.dp
                            } else {
                                250.dp
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun DetailsContent(paddingValues: PaddingValues, details: PokemonDetails) {
    val tabs = listOf(
        stringResource(R.string.about),
        stringResource(R.string.base_stats),
        stringResource(R.string.moves)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        HorizontalPager(
            state = pagerState
        ) {
            when (pagerState.currentPage) {
                0 -> {
                    AboutTab(details)
                }

                1 -> {
                    StatsTab(details)
                }

                2 -> {
                    MovesTab(details)
                }
            }
        }
    }
}

@Composable
private fun AboutTab(details: PokemonDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        val height = details.height / 10.0
        val weight = details.weight / 10.0
        AboutTabContent(
            title = stringResource(R.string.abilities),
            description = details.abilities.joinToString { it.ability.name }
        )
        AboutTabContent(
            title = stringResource(id = R.string.height),
            description = "$height m"
        )
        AboutTabContent(
            title = stringResource(id = R.string.weight),
            description = "$weight kg"
        )
        AboutTabContent(
            title = stringResource(id = R.string.types),
            description = details.types.joinToString { it.type.name }
        )
        AboutTabContent(
            title = stringResource(id = R.string.base_experience),
            description = "${details.base_experience}"
        )
    }
}

@Composable
private fun AboutTabContent(title: String, description: String) {
    val fontSize = when {
        mediaQueryWidth() <= small -> 16.sp
        mediaQueryWidth() <= normal -> 20.sp
        else -> 24.sp
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Grey,
            fontWeight = FontWeight.W400,
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.padding(20.dp))
        Text(
            text = description,
            color = Black,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
    Spacer(modifier = Modifier.padding(3.dp))
}

@Composable
private fun StatsTab(details: PokemonDetails) {
    val fontSize = when {
        mediaQueryWidth() <= small -> 16.sp
        mediaQueryWidth() <= normal -> 20.sp
        else -> 24.sp
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(details.stats.size) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = details.stats[index].stat.name,
                    color = Grey,
                    fontWeight = FontWeight.W400,
                    fontSize = fontSize
                )
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    text = details.stats[index].base_stat.toString(),
                    color = Black,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Composable
private fun MovesTab(details: PokemonDetails) {
    val fontSize = when {
        mediaQueryWidth() <= small -> 16.sp
        mediaQueryWidth() <= normal -> 20.sp
        else -> 24.sp
    }
    val moves =
        if (details.moves.size >= 21) {
            details.moves.map { it.move.name }.subList(0, 21)
        } else {
            details.moves.map { it.move.name }
        }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.Center
    ) {
        items(moves.size) { index ->
            Text(
                text = details.moves[index].move.name,
                color = Black,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize
            )
            Spacer(modifier = Modifier.padding(10.dp))
        }
    }
}